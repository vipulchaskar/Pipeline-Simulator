package com.company;

import com.company.Commons.*;

public class IQEntry {
    private InstructionInfo ins;
    private FU fuType;
    private boolean src1Ready, src2Ready;

    public InstructionInfo getIns() {
        return ins;
    }

    public void setIns(InstructionInfo ins) {
        this.ins = ins;
    }

    public FU getFuType() {
        return fuType;
    }

    public void setFuType(FU fuType) {
        this.fuType = fuType;
    }

    public boolean isSrc1Ready() {
        return src1Ready;
    }

    public void setSrc1Ready(boolean src1Ready) {
        this.src1Ready = src1Ready;
    }

    public boolean isSrc2Ready() {
        return src2Ready;
    }

    public void setSrc2Ready(boolean src2Ready) {
        this.src2Ready = src2Ready;
    }
}
