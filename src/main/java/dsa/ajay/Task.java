package dsa.ajay;

import java.io.Serializable;

public interface Task extends Serializable {
    Object execute() throws Exception;
}


