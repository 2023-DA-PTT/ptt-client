package com.ptt.entities;

import java.io.IOException;

public interface ExecutedStep {
    String getParameter(OutputArgument argument) throws IOException;
}
