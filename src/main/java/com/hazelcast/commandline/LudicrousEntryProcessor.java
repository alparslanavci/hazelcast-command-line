package com.hazelcast.commandline;

import com.hazelcast.map.EntryProcessor;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

public class LudicrousEntryProcessor implements EntryProcessor<Integer, Ludicrous, Object>, Serializable {

    private int finalSelf;

    public LudicrousEntryProcessor(int finalSelf) {
        this.finalSelf = finalSelf;
    }

    @Override
    public Object process(Map.Entry<Integer, Ludicrous> entry) {
        Ludicrous value = entry.getValue();
        value.pos[finalSelf] += 10;
        entry.setValue(value);
        return null;
    }

    @Nullable
    @Override
    public EntryProcessor<Integer, Ludicrous, Object> getBackupProcessor() {
        return null;
    }
}
