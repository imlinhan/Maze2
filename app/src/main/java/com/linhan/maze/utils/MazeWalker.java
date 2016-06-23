package com.linhan.maze.utils;

import android.net.Uri;
import android.util.Log;

import com.linhan.maze.model.CheckResult;
import com.linhan.maze.model.Node;
import com.linhan.maze.model.Point;
import com.linhan.maze.model.StepResult;
import com.linhan.maze.net.MazeNetEngine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by linhan on 16/6/20.
 */

public class MazeWalker {

    private static final String TAG = "Maze";

    List<Point> mRecord;
    Stack<Node> mStack;
    String mMazeId;
    Point mCurrentPoint;

    HashMap<Point, String> mLabelMap;

    LogTextCallback mLogCallback;

    MazeNetEngine.Maze mServer;

    boolean mIsFirstStep = true;
    boolean mIsEnd = false;

    Node mCurrentNode;

    public MazeWalker() {
        mRecord = new LinkedList<>();
        mStack = new Stack<>();
        mLabelMap = new HashMap<>();
        mServer = MazeNetEngine.getMaze();
    }

    public void start() {
        mRecord.clear();
        mStack.clear();
        mLabelMap.clear();
        mCurrentNode = new Node(0, 0);
        mStack.push(mCurrentNode);
        mIsFirstStep = true;
        mIsEnd = false;
        step();
    }

    private void step() {

        if(mIsEnd)
            return;

        if (!mStack.isEmpty()) {

            mCurrentNode = mStack.pop();

            Point point = mCurrentNode.getPoint();

            recordPoint(point);

            Call<StepResult> stepResultCall;
            if(mIsFirstStep) {
                stepResultCall = mServer.start();
                LOGD("OK lets start!");
            }
            else {
                stepResultCall = requestStep(point);
            }

            stepResultCall.enqueue(new Callback<StepResult>() {
                @Override
                public void onResponse(Response<StepResult> response, Retrofit retrofit) {
                    if(response.body() == null){
                        LOGD("request error!");
                        return;
                    }
                    if(mIsFirstStep){
                        mIsFirstStep = false;
                        com.squareup.okhttp.Response respon = response.raw();
                        Uri uri = Uri.parse(respon.request().urlString());
                        mMazeId = uri.getQueryParameter(MazeNetEngine.PARAM_S);
                        LOGD("New maze, id:" + mMazeId);
                    }
                    handleResult(response.body());
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });

        }
    }

    private void handleResult(StepResult result){

        String label = result.getLetter();
        recordLabel(mCurrentNode.getPoint(), label);

        if (result.isEnd()) {
            mIsEnd = true;
            LOGD("we found the way out!");
            return;
        }

        List<Point> adjacent = result.getAdjacent();

        if (!isEmpty(adjacent)) {
            int i = 0;
            for (Point p : adjacent) {
                if(mLabelMap.containsKey(p))
                    continue;
                Node nextNode = new Node(p);
                nextNode.setFather(mCurrentNode);
                mStack.push(nextNode);
                ++i;
            }
            if(i == 0)
                stepBack();
        } else {
            stepBack();
        }

        Log.d(TAG, createCheckPath());
        step();
    }

    public String createCheckPath() {

        StringBuilder builder = new StringBuilder();

        int size = mRecord.size();
        for (int i = 0; i < size; ++i) {
            Point point = mRecord.get(i);
            builder.append(getLabel(point));
        }

        return builder.toString();
    }

    private void recordPoint(Point point) {
        mRecord.add(point);
    }

    private void recordLabel(Point point, String label) {
        mLabelMap.put(point, label);
    }

    private String getLabel(Point point) {

        return mLabelMap.get(point);
    }

    private void stepBack() {

        Node nextNode = mStack.peek();

        Point backEndPoint;
        //Step back to (0,0)
        if(nextNode.getFather() == null){
            backEndPoint = new Point(0, 0);
        }else{
            backEndPoint = nextNode.getFather().getPoint();
        }

        Node backNode = mCurrentNode.getFather();
        while(backNode != null && !backEndPoint.equals(backNode.getPoint())){
            Point point = backNode.getPoint();
            LOGD("step back:" + point.getX() + "," + point.getY());
            mRecord.add(point);
            backNode = backNode.getFather();
        }
        LOGD("step back:" + backEndPoint.getX() + "," + backEndPoint.getY());
        mRecord.add(backEndPoint);
    }

    public boolean check() {
        String path = createCheckPath();

        LOGD(path);

        Call<CheckResult> resultCall = requestCheck(path);
        resultCall.enqueue(new Callback<CheckResult>() {
            @Override
            public void onResponse(Response<CheckResult> response, Retrofit retrofit) {
                LOGD("check result:" + response.body().isSuccess());
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
        return true;
    }

    private Call<CheckResult> requestCheck(String s) {
        return mServer.check(mMazeId, s);
    }

    private Call<StepResult> requestStep(Point point) {
        return requestStep(point.getX(), point.getY(), mMazeId);
    }

    private Call<StepResult> requestStep(int x, int y, String id) {
        LOGD("step to:" + x + "," + y);
        return mServer.step(x, y, id);
    }

    private boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public void setLogCallback(LogTextCallback logCallback) {
        this.mLogCallback = logCallback;
    }

    public interface LogTextCallback {

        void onLogText(String text);
    }

    private void LOGD(String text) {
        Log.d(TAG, text);
        if (mLogCallback != null)
            mLogCallback.onLogText(text);
    }
}
