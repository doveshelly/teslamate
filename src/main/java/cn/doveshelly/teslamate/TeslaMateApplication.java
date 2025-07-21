package cn.doveshelly.teslamate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@MapperScan("cn.doveshelly.teslamate.mapper")
public class TeslaMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeslaMateApplication.class, args);
    }
}