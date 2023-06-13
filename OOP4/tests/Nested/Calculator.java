package tests.Nested;

public class Calculator {

    private Integer num;


    public Calculator(Integer num) {
        this.num = num;
    }

    public Calculator(Calculator calculator) {
        this.num = calculator.getNum();
    }

    public void addNum(Integer num) {
        this.num = this.num + num;
    }

    public Integer getNum() {
        return num;
    }

    public void subtractNum(Integer num) {
        this.num = this.num - num;
    }
}
