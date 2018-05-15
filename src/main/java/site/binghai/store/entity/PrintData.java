package site.binghai.store.entity;


/**
 * Created by IceSea on 2018/5/15.
 * GitHub: https://github.com/IceSeaOnly
 */
public class PrintData {
    private StringBuilder sb ;

    private PrintData() {
        sb = new StringBuilder();
    }

    public static PrintData getInstance(){
        return new PrintData();
    }

    public PrintData breakLine(){
        sb.append("<BR>");
        return this;
    }

    public PrintData CutPage(){
        sb.append("<CUT>");
        return this;
    }

    public PrintData CenterBold(String content){
        sb.append("<CB>"+content+"</CB>");
        return this;
    }

    public PrintData text(String content){
        sb.append(content);
        return this;
    }

    public PrintData Center(String content){
        sb.append("<C>"+content+"</C>");
        return this;
    }


    public PrintData Bold(String content){
        sb.append("<B>"+content+"</B>");
        return this;
    }

    public PrintData Heighter(String content){
        sb.append("<L>"+content+"</L>");
        return this;
    }

    public PrintData Wilder(String content){
        sb.append("<W>"+content+"</W>");
        return this;
    }

    public PrintData QrCode(String content){
        sb.append("<QR>"+content+"</QR>");
        return this;
    }

    public PrintData toRight(String content){
        sb.append("<RIGHT>"+content+"</RIGHT>");
        return this;
    }

    public String toString(){
        return sb.toString();
    }
}
