package com.yumu.hexie.integration.posting.mapper;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-11-09 16:09
 */
public class QueryPostingSummaryMapper {

    private String userSectId;
    private String userSectName;
    private String num;

    public String getUserSectId() {
        return userSectId;
    }

    public void setUserSectId(String userSectId) {
        this.userSectId = userSectId;
    }

    public String getUserSectName() {
        return userSectName;
    }

    public void setUserSectName(String userSectName) {
        this.userSectName = userSectName;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
