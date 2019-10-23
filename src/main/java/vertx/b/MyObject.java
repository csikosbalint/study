package vertx.b;

public class MyObject {
  private String msg;

  public MyObject(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return msg;
  }
}
