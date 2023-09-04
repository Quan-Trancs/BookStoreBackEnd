package quantran.api.page;

import quantran.api.model.BookModel;

import java.util.List;

public class Paginate {
    private List<BookModel> data;
    private int total;

    public Paginate(List<BookModel> data, int total) {
        this.data = data;
        this.total = total;
    }
    public List<BookModel> getData() {
        return data;
    }

    public void setData(List<BookModel> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
