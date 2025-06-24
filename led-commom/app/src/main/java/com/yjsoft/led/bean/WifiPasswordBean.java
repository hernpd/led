package com.yjsoft.led.bean;

public class WifiPasswordBean {

    /**
     * ack : {"param_wifi":{"type":2,"user":"ledled","pwd":"12345678","user_coe":"","pwd_coe":"12345678"}}
     * sno : 4294901762
     */

    private AckBean ack;
    private long sno;

    public AckBean getAck() {
        return ack;
    }

    public void setAck(AckBean ack) {
        this.ack = ack;
    }

    public long getSno() {
        return sno;
    }

    public void setSno(long sno) {
        this.sno = sno;
    }

    public static class AckBean {
        /**
         * param_wifi : {"type":2,"user":"ledled","pwd":"12345678","user_coe":"","pwd_coe":"12345678"}
         */

        private ParamWifiBean param_wifi;

        public ParamWifiBean getParam_wifi() {
            return param_wifi;
        }

        public void setParam_wifi(ParamWifiBean param_wifi) {
            this.param_wifi = param_wifi;
        }

        public static class ParamWifiBean {
            /**
             * type : 2
             * user : ledled
             * pwd : 12345678
             * user_coe :
             * pwd_coe : 12345678
             */

            private int type;
            private String user;
            private String pwd;
            private String user_coe;
            private String pwd_coe;

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public String getPwd() {
                return pwd;
            }

            public void setPwd(String pwd) {
                this.pwd = pwd;
            }

            public String getUser_coe() {
                return user_coe;
            }

            public void setUser_coe(String user_coe) {
                this.user_coe = user_coe;
            }

            public String getPwd_coe() {
                return pwd_coe;
            }

            public void setPwd_coe(String pwd_coe) {
                this.pwd_coe = pwd_coe;
            }
        }
    }
}
