# 简单实现RPC调用框架

基于java实现的rpc框架实现
- 使用hessian实现对象的序列化
- 使用snappy进行压缩与解压缩
- 使用netty进行节点间的网络通信
- 使用zookeeper作为服务的注册中心
- 使用jdk的代理实现消费端代理对象的生成
