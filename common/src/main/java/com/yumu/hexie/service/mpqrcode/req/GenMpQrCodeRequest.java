package com.yumu.hexie.service.mpqrcode.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenMpQrCodeRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7781646198921156018L;
	
//	{
//	    "expire_seconds": 604800,
//	    "action_name": "QR_STR_SCENE",
//	    "action_info": {
//	        "scene": {
//	            "scene_str": "210601001248955728|328.60|上海农工商旺都物业管理有限公司"
//	        }
//	    }
//	}
	
	@JsonProperty("expire_seconds")
	private Long expireSeconds;	//过期时间，秒，最大30天
	@JsonProperty("action_name")
	private String actionName;	//码类型
	@JsonProperty("action_info")
	private ActionInfo actionInfo;
	
	public static class ActionInfo {
		
		private Scene scene;

		public Scene getScene() {
			return scene;
		}

		public void setScene(Scene scene) {
			this.scene = scene;
		}

		@Override
		public String toString() {
			return "ActionInfo [scene=" + scene + "]";
		}
		
	}

	public static class Scene {
		
		@JsonProperty("scene_str")
		private String sceneStr;

		public String getSceneStr() {
			return sceneStr;
		}
		public void setSceneStr(String sceneStr) {
			this.sceneStr = sceneStr;
		}
		@Override
		public String toString() {
			return "Scene [sceneStr=" + sceneStr + "]";
		}
		
	}

	public Long getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(Long expireSeconds) {
		this.expireSeconds = expireSeconds;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public ActionInfo getActionInfo() {
		return actionInfo;
	}

	public void setActionInfo(ActionInfo actionInfo) {
		this.actionInfo = actionInfo;
	}

	@Override
	public String toString() {
		return "GenMpQrCodeRequest [expireSeconds=" + expireSeconds + ", actionName=" + actionName + ", actionInfo="
				+ actionInfo + "]";
	}
	
	
}
