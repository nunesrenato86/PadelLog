package com.renatonunes.padellog.domain;

/**
 * Created by Renato on 30/08/2016.
 */
public class ChampionshipSummary {
    private int draw;
    private int round64;
    private int round32;
    private int round16;
    private int round8;
    private int quarterfinals;
    private int semifinals;
    private int vice;
    private int champion;
    private int noResult;

    public ChampionshipSummary() {
        init();
    }

    public void init(){
        this.draw = 0;
        this.round64 = 0;
        this.round32 = 0;
        this.round16 = 0;
        this.round8 = 0;
        this.quarterfinals = 0;
        this.semifinals = 0;
        this.vice = 0;
        this.champion = 0;
        this.noResult = 0;
    };

    private void addDraw(){
        this.draw++;
    }

    private void addRound64(){
        this.round64++;
    }

    private void addRound32(){
        this.round32++;
    }

    private void addRound16(){
        this.round16++;
    }

    private void addRound8(){
        this.round8++;
    }

    private void addQuarterfinals(){
        this.quarterfinals++;
    }

    private void addSemifinals(){
        this.semifinals++;
    }

    private void addVice(){
        this.vice++;
    }

    private void addChampion(){
        this.champion++;
    }

    private void addNoResult(){
        this.noResult++;
    }

    public int getDraw() {
        return draw;
    }

    public int getRound64() {
        return round64;
    }

    public int getRound32() {
        return round32;
    }

    public int getRound16() {
        return round16;
    }

    public int getRound8() {
        return round8;
    }

    public int getQuarterfinals() {
        return quarterfinals;
    }

    public int getSemifinals() {
        return semifinals;
    }

    public int getVice() {
        return vice;
    }

    public int getChampion() {
        return champion;
    }

    public int getNoResult() {
        return noResult;
    }

    public void addResult(int result){
        switch(result) {
            case 0:
                this.addDraw();
                break;
            case 1:
                this.addRound64();
                break;
            case 2:
                this.addRound32();
                break;
            case 3:
                this.addRound16();
                break;
            case 4:
                this.addRound8();
                break;
            case 5:
                this.addQuarterfinals();
                break;
            case 6:
                this.addSemifinals();
                break;
            case 7:
                this.addVice();
                break;
            case 8:
                this.addChampion();
                break;
            default:
                this.addNoResult();
//                break;
        }
    }
}
