package an.rentalinhaee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "mailExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);    // 기본적으로 실행 대기 중인 Thread 개수
        executor.setMaxPoolSize(5);     // 동시에 동작하는 최대 Thread 개수
        executor.setQueueCapacity(10);  // CorePool의 크기를 넘어서면 큐에 저장하는데 그 큐의 최대 용량
        executor.setThreadNamePrefix("Async MailExecutor-");
        executor.initialize();

        return executor;
    }
}
