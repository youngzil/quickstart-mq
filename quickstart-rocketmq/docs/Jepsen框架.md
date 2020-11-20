Jepsen框架

[Jepsen官网](https://jepsen.io/)  
[Jepsen Github](https://github.com/jepsen-io)  
[Jepsen文档](http://jepsen-io.github.io/jepsen/)  
[Jepsen Tutorial教程](https://github.com/jepsen-io/jepsen/blob/main/doc/tutorial/index.md)  

A framework for distributed systems verification, with fault injection

带有故障注入的分布式系统验证框架

Jepsen is a Clojure library. A test is a Clojure program which uses the Jepsen library to set up a distributed system, run a bunch of operations against that system, and verify that the history of those operations makes sense. Jepsen has been used to verify everything from eventually-consistent commutative databases to linearizable coordination systems to distributed task schedulers. It can also generate graphs of performance and availability, helping you characterize how a system responds to different faults. See jepsen.io for examples of the sorts of analyses you can carry out with Jepsen.

Jepsen是Clojure库。 测试是一个Clojure程序，它使用Jepsen库来设置分布式系统，对该系统运行一堆操作，并验证这些操作的历史记录是否有意义。 Jepsen已用于验证从最终一致的可交换数据库到线性化协调系统再到分布式任务调度程序的所有内容。 它还可以生成性能和可用性的图表，帮助您表征系统对不同故障的响应方式。 有关可以使用Jepsen进行的各种分析的示例，请参见jepsen.io。




Jepsen 框架主要是在特定故障下验证系统是否满足一致性。

Jepsen 验证系统由 6 个节点组成，一个控制节点（Control Node），五个 DB 节点（DB Node）。
- 控制节点可以通过 SSH 登录到 DB 节点，通过控制节点的控制，可以在 DB 节点完成分布式系统的下载，部署，组成一个待测试的集群。
- 测试开始后，控制节点会创建一组 Worker 进程，每一个 Worker 都有自己的分布式系统客户端。Generator 产生每个客户端执行的操作，客户端进程将操作应用于待测试的分布式系统。每个操作的开始和结束以及操作结果记录在历史记录中。同时，一个特殊的 Client 进程 Nemesis 将故障引入系统。
- 测试结束后， Checker 分析历史记录是否正确，是否符合一致性。






