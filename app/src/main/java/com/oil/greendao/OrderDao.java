package com.oil.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.oil.Bean.Order;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ORDER".
*/
public class OrderDao extends AbstractDao<Order, Long> {

    public static final String TABLENAME = "ORDER";

    /**
     * Properties of entity Order.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Goumaizenhao = new Property(1, String.class, "goumaizenhao", false, "GOUMAIZENHAO");
        public final static Property Qiyoubiaohao = new Property(2, String.class, "qiyoubiaohao", false, "QIYOUBIAOHAO");
        public final static Property Sulian = new Property(3, String.class, "sulian", false, "SULIAN");
        public final static Property Cardid = new Property(4, String.class, "cardid", false, "CARDID");
        public final static Property Name = new Property(5, String.class, "name", false, "NAME");
        public final static Property Goumairenzhaopian = new Property(6, String.class, "goumairenzhaopian", false, "GOUMAIRENZHAOPIAN");
        public final static Property Cardpic = new Property(7, String.class, "cardpic", false, "CARDPIC");
        public final static Property Xiaoshouren = new Property(8, String.class, "xiaoshouren", false, "XIAOSHOUREN");
        public final static Property Xiaoshourenhao = new Property(9, String.class, "xiaoshourenhao", false, "XIAOSHOURENHAO");
        public final static Property Zhiwen = new Property(10, String.class, "zhiwen", false, "ZHIWEN");
    }


    public OrderDao(DaoConfig config) {
        super(config);
    }
    
    public OrderDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ORDER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," + // 0: id
                "\"GOUMAIZENHAO\" TEXT," + // 1: goumaizenhao
                "\"QIYOUBIAOHAO\" TEXT," + // 2: qiyoubiaohao
                "\"SULIAN\" TEXT," + // 3: sulian
                "\"CARDID\" TEXT," + // 4: cardid
                "\"NAME\" TEXT," + // 5: name
                "\"GOUMAIRENZHAOPIAN\" TEXT," + // 6: goumairenzhaopian
                "\"CARDPIC\" TEXT," + // 7: cardpic
                "\"XIAOSHOUREN\" TEXT," + // 8: xiaoshouren
                "\"XIAOSHOURENHAO\" TEXT," + // 9: xiaoshourenhao
                "\"ZHIWEN\" TEXT);"); // 10: zhiwen
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ORDER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Order entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String goumaizenhao = entity.getGoumaizenhao();
        if (goumaizenhao != null) {
            stmt.bindString(2, goumaizenhao);
        }
 
        String qiyoubiaohao = entity.getQiyoubiaohao();
        if (qiyoubiaohao != null) {
            stmt.bindString(3, qiyoubiaohao);
        }
 
        String sulian = entity.getSulian();
        if (sulian != null) {
            stmt.bindString(4, sulian);
        }
 
        String cardid = entity.getCardid();
        if (cardid != null) {
            stmt.bindString(5, cardid);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(6, name);
        }
 
        String goumairenzhaopian = entity.getGoumairenzhaopian();
        if (goumairenzhaopian != null) {
            stmt.bindString(7, goumairenzhaopian);
        }
 
        String cardpic = entity.getCardpic();
        if (cardpic != null) {
            stmt.bindString(8, cardpic);
        }
 
        String xiaoshouren = entity.getXiaoshouren();
        if (xiaoshouren != null) {
            stmt.bindString(9, xiaoshouren);
        }
 
        String xiaoshourenhao = entity.getXiaoshourenhao();
        if (xiaoshourenhao != null) {
            stmt.bindString(10, xiaoshourenhao);
        }
 
        String zhiwen = entity.getZhiwen();
        if (zhiwen != null) {
            stmt.bindString(11, zhiwen);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Order entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String goumaizenhao = entity.getGoumaizenhao();
        if (goumaizenhao != null) {
            stmt.bindString(2, goumaizenhao);
        }
 
        String qiyoubiaohao = entity.getQiyoubiaohao();
        if (qiyoubiaohao != null) {
            stmt.bindString(3, qiyoubiaohao);
        }
 
        String sulian = entity.getSulian();
        if (sulian != null) {
            stmt.bindString(4, sulian);
        }
 
        String cardid = entity.getCardid();
        if (cardid != null) {
            stmt.bindString(5, cardid);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(6, name);
        }
 
        String goumairenzhaopian = entity.getGoumairenzhaopian();
        if (goumairenzhaopian != null) {
            stmt.bindString(7, goumairenzhaopian);
        }
 
        String cardpic = entity.getCardpic();
        if (cardpic != null) {
            stmt.bindString(8, cardpic);
        }
 
        String xiaoshouren = entity.getXiaoshouren();
        if (xiaoshouren != null) {
            stmt.bindString(9, xiaoshouren);
        }
 
        String xiaoshourenhao = entity.getXiaoshourenhao();
        if (xiaoshourenhao != null) {
            stmt.bindString(10, xiaoshourenhao);
        }
 
        String zhiwen = entity.getZhiwen();
        if (zhiwen != null) {
            stmt.bindString(11, zhiwen);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public Order readEntity(Cursor cursor, int offset) {
        Order entity = new Order( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // goumaizenhao
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // qiyoubiaohao
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sulian
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // cardid
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // name
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // goumairenzhaopian
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // cardpic
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // xiaoshouren
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // xiaoshourenhao
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // zhiwen
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Order entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setGoumaizenhao(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setQiyoubiaohao(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSulian(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCardid(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setGoumairenzhaopian(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCardpic(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setXiaoshouren(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setXiaoshourenhao(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setZhiwen(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Order entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Order entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Order entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
