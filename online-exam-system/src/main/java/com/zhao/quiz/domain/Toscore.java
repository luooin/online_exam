package com.zhao.quiz.domain;

/**
 * @Auther: fangsiyi
 * @Date: 2024.1.21
 * @Description:
 */
public class Toscore {
    private int toscoreId;
    private int paperId;
    private int toscore;

    public Toscore(int paperId, int toscore) {
        this.paperId = paperId;
        this.toscore = toscore;
    }

    public Toscore() {
    }

    public int getToscoreId() {
        return toscoreId;
    }

    public void setToscoreId(int toscoreId) {
        this.toscoreId = toscoreId;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public int getToscore() {
        return toscore;
    }

    public void setToscore(int toscore) {
        this.toscore = toscore;
    }
}
