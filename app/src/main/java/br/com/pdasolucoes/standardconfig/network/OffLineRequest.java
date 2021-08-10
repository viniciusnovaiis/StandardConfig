package br.com.pdasolucoes.standardconfig.network;

import java.util.Date;
import br.com.pdasolucoes.standardconfig.network.enums.RequestInfo;
import br.com.pdasolucoes.standardconfig.network.enums.RequestType;

public abstract class OffLineRequest extends SoapRequestBase {

	private int id;
	private String service;
	private String action;
	private String body;
	private Date dateTime;
	
	public OffLineRequest(int id, String service, String action, String body, Date dateTime) {
		super();
		
		this.id = id;
		this.service = service;
		this.action = action;
		this.body = body;
		this.dateTime = dateTime;
	}
	
	public int getId() {
		return this.id;
	}
	
	@Override
	protected RequestInfo getRequestInfo() {
		return null;
	}
	
	@Override
	public String getService(){
		return this.service;
	}
	
	@Override
	public String getAction(){
		return this.action;
	}
	
	@Override
	public RequestType getRequestType(){
		return RequestType.OffLine;
	}
	
	@Override
	public String getDescription(){
		return "";
	}

}
