Implement a mutual exclusion service using Roucairol and CarvalhoÕs distributed mutual exclusion algorithm. Your service should provide two function calls to the application: cs-enter and cs-leave. The first function call cs-enter allows an application to request permission to start executing its critical section. The function call is blocking and returns only when the invoking application can execute its critical section. The second function call cs-leave allows an application to inform the service that it has finished executing its critical section. Implementation Details: Design your program so that each process or node consists of two separate modulesÐone module that implements the application (requests and executes critical sections) and one module that implements the mutual exclusion algorithm (coordinates critical section executions of all processes so that they do not overlap). Intuitively, the two modules interact using cs-enter and cs-leave function calls. Each module in turn may be implemented using one or more threads. It should be possible to swap your application module with our own application module and your program should still compile and run correctly!

####Testing: Design a mechanism to test the correctness of your implementation. Your testing mechanism should ascertain that at most one process is in its critical section at any time. It should be as automated as possible and should require minimal human intervention. For example, visual inspection of log files to verify the correctness of the execution will not be acceptable. You will be graded on how accurate and automated your testing mechanism is

###Project Evaluation: Compare the performance of the mutual exclusion algorithm with respect to message complexity, response time and system throughput using experiments for various values of system parameters, namely n, d and c. Display the results of your experiments by plotting appropriate graphs. Note that each point in the graph should be obtained by averaging over multiple runs.



To run the program you need follow these steps:

1. Modify the netid in launcher.sh and cleanup.sh to your netid
2. Modify the PROJDIR and CONFIGLOCAL in launcher.sh and cleanup.sh to your working directory
3. Run this command chmod +x build.sh launcher.sh cleanup.sh cleanFiles.sh
    - if at any point in running the scripts you recieve an error that is something like "token error: token is ""
    and you modify your scripts in Windows then you are experiencing conflict from the use of carriage returns.
    Run the following command on the file and try again to remove the carriage returns (<filename> is replaced with the file's name):
        sed -i -e 's/\r$//' <filename>
    - Enable graphic display with your tunneling software [ex in mobaxterm, enable x server] to view terminal popup windows
4. Run javac *.java
5. Run ./launcher.sh to run the program
6. Run ./cleanup.sh to kill erroneous processes