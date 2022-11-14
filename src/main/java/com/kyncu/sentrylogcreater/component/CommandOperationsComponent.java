package com.kyncu.sentrylogcreater.component;

import com.kyncu.sentrylogcreater.input.CommandInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommandOperationsComponent {

    @Value("${default.user.count}")
    private String defaultUserCount;

    @Value("${default.loop.count}")
    private String defaultLoopCount;

    public CommandInput getCommandInputs() {
        CommandInput commandInput = new CommandInput();
        commandInput.setUserCount(Integer.parseInt(getUserCount()));
        commandInput.setTotalCount(Integer.parseInt(getTotalLoopCount()));
        return commandInput;
    }

    private String getUserCount() {
        return getPropertyValue("userCount", defaultUserCount);
    }

    private String getTotalLoopCount() {
        return getPropertyValue("loopCount", defaultLoopCount);
    }
    private String getPropertyValue(String property, String defaultValue) {
        try {
            String value = getSystemProperty(property);
            if(value == null) throw new Exception();

            return value;
        } catch (Exception e) {
            log.error(property + " not getting from system. Default value 5 setted.");
            return defaultValue;
        }
    }

    private String getSystemProperty(String property) throws Exception {
        try {
            return System.getProperty(property);
        }catch (Exception e) {
            throw new Exception(property + " not found in system properties.");
        }
    }
}
