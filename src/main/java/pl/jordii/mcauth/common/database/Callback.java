package pl.jordii.mcauth.common.database;

public interface Callback<T> {

    void accept(T result);

    default void onFailure(Throwable cause) {
        cause.printStackTrace();
    }

}
