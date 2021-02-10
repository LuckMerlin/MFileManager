package com.csdk.api.bean;

/**
 * Create LuckMerlin
 * Date 11:42 2020/9/23
 * TODO
 */
public final class Tag {
    private final String title;

    public Tag(){
        this(null);
    }

    public Tag(String title){
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (null!=obj){
            String title = null;
            if (obj instanceof Tag){
                title=((Tag)obj).getTitle();
            }
            String currTitle=this.title;
            return (null==title&&null==currTitle)||(null!=title&&null!=currTitle&&title.equals(currTitle));
        }
        return super.equals(obj);
    }
}
