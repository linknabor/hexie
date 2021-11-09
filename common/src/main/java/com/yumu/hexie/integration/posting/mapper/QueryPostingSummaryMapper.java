package com.yumu.hexie.integration.posting.mapper;

import java.math.BigInteger;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-11-09 16:09
 */
public class QueryPostingSummaryMapper {

    private BigInteger userSectId;
    private String userSectName;
    private BigInteger num;

    public QueryPostingSummaryMapper(BigInteger userSectId, String userSectName,
                                     BigInteger num) {
        super();
        this.userSectId = userSectId;
        this.userSectName = userSectName;
        this.num = num;
    }

    public BigInteger getUserSectId() {
        return userSectId;
    }

    public void setUserSectId(BigInteger userSectId) {
        this.userSectId = userSectId;
    }

    public String getUserSectName() {
        return userSectName;
    }

    public void setUserSectName(String userSectName) {
        this.userSectName = userSectName;
    }

    public BigInteger getNum() {
        return num;
    }

    public void setNum(BigInteger num) {
        this.num = num;
    }
}
