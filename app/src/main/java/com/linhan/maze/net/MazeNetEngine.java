package com.linhan.maze.net;

import com.linhan.maze.model.CheckResult;
import com.linhan.maze.model.StepResult;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by linhan on 2016/6/20.
 */
public class MazeNetEngine {

    private static final String API = "https://challenge.flipboard.com";

    private static final String PATH_START = "/start";
    private static final String PATH_STEP = "/step";
    private static final String PATH_CHECK = "/check";

    private static final String PARAM_X = "x";
    private static final String PARAM_Y = "y";
    public static final String PARAM_S = "s";
    private static final String PARAM_GUESS = "guess";

    public interface Maze{

        @GET(PATH_START)
        Call<StepResult> start();

        @GET(PATH_STEP)
        Call<StepResult> step(@Query(PARAM_X) int x, @Query(PARAM_Y) int y, @Query(PARAM_S) String s);

        @GET(PATH_CHECK)
        Call<CheckResult> check(@Query(PARAM_S) String s, @Query(PARAM_GUESS) String guess);
    }

    public static Maze getMaze(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(Maze.class);
    }
}
