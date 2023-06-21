package com.anner.comm.info;

import java.util.List;

/**
 * 已发布服务的信息汇总
 */
public class CommPublishInfo {

     private final String domain;
     private final List<CommServerInfo> infoList;

     public CommPublishInfo(String domain, List<CommServerInfo> infoList) {
          this.domain = domain;
          this.infoList = infoList;
     }

     public String getDomain() {
          return domain;
     }

     public List<CommServerInfo> getInfoList() {
          return infoList;
     }
}
