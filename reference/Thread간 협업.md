👓 Thread간 협업

경우에 따라 두 스레드가 **교대**로 번갈아가며 실행해야 할 경우가 있다. (정확한 교대작업이 필요한 경우, 한 스레드가 작업이 끝나며 상대방 스레드의 일시정지를 풀어주고 자신은 일시정지로 만들어야 한다.)

**공유객체** 를 사용하여 두 스레드가 작업할 내용을 각각 동기화 메소드로 구분해 놓은 후 한 **스레드가 작업이 완료되면 notify()메서드를 호출**한다.

notify() => 일시 정지 상태에 있는 다른 스레드를 실행 대기 상태로 만듬

wait() => 스레드를 일시 정지 상태로 만듬

위 두 메서드는 Thread 클래스가 아닌 Object 클래스에 선언된 메소드이므로 모든 공유 객체에서 호출이 가능하다. (동기화블록에서만 사용 가능)



스레드 작업 내용을 동기화 메소드로 작성한 공유객체

```java
public class WorkObject {
    public synchronized void methodA() {
        System.out.println("ThreadA의 methodA() 작업 실행");
        notify(); // 일시정지 상태에 있는 ThreadB를 실행 대기상태로 만듬 
        try {
            wait(); // ThreadA를 일시 정지 상태로 만듬 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void methodB() {
        System.out.println("ThreadB의 methodB() 작업 실행");
        notify(); // 일시정지 상태에 있는 ThreadA를 실행 대기상태로 만듬
        try {
            wait(); // ThreadB를 일시 정지 상태로 만듬
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```



ThreadA

```java
public class ThreadA extends Thread{
    private WorkObject workObject;

    ThreadA(WorkObject workObject) {
        this.workObject = workObject;
    }
    
    public void run() {
        for(int i=0; i<10; i++) {
            workObject.methodA(); // 공유객체의 methodA를 반복적으로 호출 
        }
    }
}
```



ThreadB

```java
public class ThreadB extends Thread{
    private WorkObject workObject;

    ThreadB(WorkObject workObject) {
        this.workObject = workObject;
    }
    
    public void run() {
        for(int i=0; i<10; i++) {
            workObject.methodB(); // 공유객체의 methodB를 반복적으로 호출 
        }
    }
}
```



Main

```java
public class WaitNotifyExample {
    public static void main(String[] args) {
        WorkObject sharedObject = new WorkObject();
        
        ThreadA threadA = new ThreadA(sharedObject);
        ThreadB threadB = new ThreadB(sharedObject);
        
        threadA.start();
        threadB.start();
    }
}
```





Result

```
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
ThreadA의 methodA() 작업 실행
ThreadB의 methodB() 작업 실행
```

