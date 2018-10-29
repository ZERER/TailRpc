# tailRpc
这是一个基于netty,zookeeper的非常简易的rpc框架
### 启动要求
* jdk1.8+
* maven3.0+
* 使用zookeeper作为注册中心
### 示例
#### 定义接口及实现
``` 
public interface HelloService {
     String hello(Integer x); 
 } 
 
@RpcService(Value = HelloService.class)
 public class HelloServiceImpl implements HelloService {
     @Override
     public String hello(Integer x) {
         System.out.println("hello " + x);
         return "RPC Server :" + x;
     }
 }
 ```
 #### 服务端启动
 ```
    @Test
    public void testStartHelloServiceByServerName() {
        RpcServerBootstrap bootstrap = new RpcServerBootstrap();
        bootstrap.register("192.168.88.12:2181")
                .addService(new HelloServiceImpl())
                .serverAddr("127.0.0.1:8080")
                .serverName("test")
                .start();
    }
```
#### 客户端调用
```
    @Test
    public void testRequest() throws InterruptedException {
        RpcClient client = new RpcClient("192.168.88.12:2181")
                .setRequestTimeOut(10, TimeUnit.SECONDS);
        HelloService service = client.create(HelloService.class);
        String result = service.hello(1000);
        System.out.println("testRequest result = " + result);
        Thread.sleep(1000 * 60* 60 * 24);

    }
```
#### 客户端异步调用
```
    private RpcClient client;
    @Before
    public void before(){
        client = new RpcClient("192.168.88.12:2181")
                .setRequestTimeOut(10, TimeUnit.SECONDS);
    }
    @Test
    public void testAsyncClient() throws ExecutionException, InterruptedException {
        AsyncProxy proxy = client.createAsyncProxy(HelloService.class);
        Future future = proxy.asyncInvoke("hello", 10).completedListener((result, e)->{
            if (e == null){
                System.out.println("执行完成");
            }
        });
        Object result = future.get();
        System.out.println("result = " + result);
        Thread.sleep(1000 * 60 * 60 * 24);

    }
```

### 并发调用
```
 final RpcClient client = new RpcClient("192.168.88.12:2181");

    ThreadPoolExecutor pool = RpcThreadPool.getExecutor(10, -1, "ConcurrentThreadPool");

    @Test
    public void testConcurrent() throws ExecutionException, InterruptedException {


        long start = System.currentTimeMillis();
        List<Future> futures = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            final int x = i;
            Future<?> future = pool.submit(() -> {
                AsyncProxy proxy = client.createAsyncProxy(HelloService.class);
                RpcFuture rpcFuture = proxy.asyncInvoke("hello", x);
                try {
                    System.out.println("result=" + rpcFuture.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
            futures.add(future);
        }

        futures.forEach(f->{
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - start));
    }

```
