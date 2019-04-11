package android.com.glide37;

public interface IGlideListener {
    void onError(String model, Exception e, long stime);
    void onSuccess();
}
