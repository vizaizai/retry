#### Retry-重试工具

##### 1. 特性

+ 同步和异步重试、异步重试通过回调方法的方式返回
+ 多间隔模式（等差、等比、cron表达式等）
+ 自定义重试触发条件
+ 宕机重启异步重试

##### 2. 安装

``` xml
<dependency>
    <groupId>com.github.vizaizai</groupId>
    <artifactId>retry</artifactId>
    <version>1.1.4</version>
</dependency>
```

##### 3. 使用

###### 同步重试: 
​		无返回值业务处理器为`VProcessor`, 如是业务方法有返回值则使用`Processor<T>`作为`inject()`的入参。

``` java
// 无返回值，没有间隔时间，发生RuntimeException或者Error时触发重试
Retry<Void> retry1 = Retry.inject(() -> {
    System.out.println("执行业务方法");
    if (Utils.getRandom(5,1) > 3) {
        throw new RuntimeException("业务出错");
    }
});
retry1.execute();
```

###### 异步重试:
​		调用`async(Callback<T> callback)`并传入回调方法。`T`是业务方法返回值类型。

``` java
Retry<String> asyncRetry = Retry.inject(() -> {
            System.out.println("执行业务方法");
            if (Utils.getRandom(5,1) > 3) {
                throw new RuntimeException("业务出错");
            }
            return "hello";
        });
asyncRetry.async(result -> {
    System.out.println("异步重试回调");
});
asyncRetry.execute();
```

###### 最大重试次数和间隔模式:
​		最大重试次数默认3次，间隔模式默认为`basic`-固定间隔,间隔时间为0s,也就是无间隔,Retry同时还支持: 等差模式`arithmetic`-以等差数列计算Sn的方式、等比模式`geometric`-以等比数列计算Sn的方式 和 Cron模式`cron`-以cron表达式计算下次执行时间。

``` java
Retry.inject(new MyProcessor())
    //.mode(Modes.cron("13,37,58 * * * * ?")) //cron模式，不支持年份
    //.mode(Modes.basic(1))// 基础模式，间隔一秒
    .mode(Modes.arithmetic(1, 0, ChronoUnit.SECONDS)) // 等差模式，首项1，公差0，单位S
    //.mode(Modes.geometric(1D, 2D, ChronoUnit.SECONDS)) // 等比模式，首项1，公比2，单位S
    .max(10) // 最大重试次数
    .async(new MyCallback())
    .retryFor(RetryException.class)
    .execute();
```

###### 触发条件：
​		 通过`retryFor()`指定发生哪些异常触发重试(包含子类)，默认发生RuntimeException或者Error时触发重试。以下代码不会触发重试，因为RetryException也是继承RuntimeException，IllegalArgumentException不是它或它的子类。

``` java
Retry<Void> retry1 = Retry.inject(() -> {
    if (Utils.getRandom(5,1) > 3) {
        throw new IllegalArgumentException("业务出错");
    }
});
retry1.(RetryException.class)
      .execute();
```







