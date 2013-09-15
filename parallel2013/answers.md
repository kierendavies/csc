# Answers to questions

## System information

Hardware used was a Lenovo ThinkPad X230 Table with Intel Core i5-3320M CPU @ 2.60GHz with four physical cores and hyperthreading.

Software was GNU/Linux kernel version 3.10.10-1-ARCH x86\_64 with Java version 1.7.0\_40, OpenJDK Runtime Environment (IcedTea 2.4.1) (ArchLinux build 7.u40\_2.4.1-3-x86\_64).

As few background processes as possible were running when benchmarking.

## Sum benchmark results

| Size      | Sequential time | Threaded time |
|----------:|----------------:|--------------:|
|    500000 |        0.003073 |      0.008352 |
|   1000000 |        0.004353 |      0.009805 |
|  10000000 |        0.009334 |      0.014960 |
| 100000000 |        0.069793 |      0.086996 |

We note that the threaded implementation is consistently slower by a factor close to 2.

## Factor benchmark results

| Size    | Sequential time | Threaded time |
|--------:|----------------:|--------------:|
|  500000 |        2.429119 |      1.238081 |
| 1000000 |        7.271094 |      3.199274 |
| 2000000 |       19.287862 |      9.074647 |

The threaded implementation performs better.  This is because, unlike previously, the work is very CPU-intensive, so the overhead of thread management is mitigated by the gain of splitting the work.

## FactorSequential with different sequential cutoffs

| Cutoff  | Time     |
|--------:|---------:|
|       1 | 3.299662 |
|   10000 | 3.236948 |
| 1000000 | 7.293642 |

A very small cutoff of 1 is negligibly slower than 10000, which is optimal here, because although the work distribution between cores is marginally better there is also slightly more thread management overhead.  A cutoff of 1000000 is as slow as the sequential implementation because only one thread is spawned to do all the work.
