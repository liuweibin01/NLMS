package resource.bean.pub.base;

import java.io.Serializable;

/**
 * This is an object that contains data related to the ROLE_INFO table. Do not
 * modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class table="ROLE_INFO"
 */

public abstract class BaseRoleInfo implements Serializable {

	public static String REF = "RoleInfo";
	public static String PROP_STATUS = "status";
	public static String PROP_LAST_UPD_TLR = "lastUpdTlr";
	public static String PROP_TIMESTAMPS = "timestamps";
	public static String PROP_LAST_UPD_FUNC = "lastUpdFunc";
	public static String PROP_MISCFLGS = "miscflgs";
	public static String PROP_ROLE_TYPE = "roleType";
	public static String PROP_ROLE_NAME = "roleName";
	public static String PROP_LAST_UPD_DATE = "lastUpdDate";
	public static String PROP_EFFECT_DATE = "effectDate";
	public static String PROP_MISC = "misc";
	public static String PROP_BRCLASS = "brclass";
	public static String PROP_EXPIRE_DATE = "expireDate";
	public static String PROP_ID = "id";

	// constructors
	public BaseRoleInfo() {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseRoleInfo(java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize() {
	}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String roleName;
	private java.lang.String status;
	private java.util.Date effectDate;
	private java.util.Date expireDate;
	private java.lang.String roleType;
	private java.lang.String brclass;
	private java.util.Date lastUpdDate;
	private java.lang.String lastUpdFunc;
	private java.lang.String lastUpdTlr;
	private java.util.Date timestamps;
	private java.lang.String miscflgs;
	private java.lang.String misc;
	/** add by zhiyang.he 修改锁定状态 2012-09-6 begin */
	private String lock;
	private String del;
	private java.lang.String st;
	private java.util.Date crtDt;
	private java.lang.String lastUpdTms;
	private java.lang.String lastUpdOper;

	/** add by zhiyang.he 修改锁定状态 2012-09-6 end */

	/**
	 * Return the unique identifier of this class
	 * 
	 * @hibernate.id generator-class="assigned" column="ROLE_ID"
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * 
	 * @param id
	 *            the new ID
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}

	/**
	 * Return the value associated with the column: ROLE_NAME
	 */
	public java.lang.String getRoleName() {
		return roleName;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	/**
	 * Set the value related to the column: ROLE_NAME
	 * 
	 * @param roleName
	 *            the ROLE_NAME value
	 */
	public void setRoleName(java.lang.String roleName) {
		this.roleName = roleName;
	}

	/**
	 * Return the value associated with the column: STATUS
	 */
	public java.lang.String getStatus() {
		return status;
	}

	/**
	 * Set the value related to the column: STATUS
	 * 
	 * @param status
	 *            the STATUS value
	 */
	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	/**
	 * Return the value associated with the column: EFFECT_DATE
	 */
	public java.util.Date getEffectDate() {
		return effectDate;
	}

	/**
	 * Set the value related to the column: EFFECT_DATE
	 * 
	 * @param effectDate
	 *            the EFFECT_DATE value
	 */
	public void setEffectDate(java.util.Date effectDate) {
		this.effectDate = effectDate;
	}

	/**
	 * Return the value associated with the column: EXPIRE_DATE
	 */
	public java.util.Date getExpireDate() {
		return expireDate;
	}

	/**
	 * Set the value related to the column: EXPIRE_DATE
	 * 
	 * @param expireDate
	 *            the EXPIRE_DATE value
	 */
	public void setExpireDate(java.util.Date expireDate) {
		this.expireDate = expireDate;
	}

	/**
	 * Return the value associated with the column: ROLE_TYPE
	 */
	public java.lang.String getRoleType() {
		return roleType;
	}

	/**
	 * Set the value related to the column: ROLE_TYPE
	 * 
	 * @param roleType
	 *            the ROLE_TYPE value
	 */
	public void setRoleType(java.lang.String roleType) {
		this.roleType = roleType;
	}

	/**
	 * Return the value associated with the column: BRCLASS
	 */
	public java.lang.String getBrclass() {
		return brclass;
	}

	/**
	 * Set the value related to the column: BRCLASS
	 * 
	 * @param brclass
	 *            the BRCLASS value
	 */
	public void setBrclass(java.lang.String brclass) {
		this.brclass = brclass;
	}

	/**
	 * Return the value associated with the column: LAST_UPD_DATE
	 */
	public java.util.Date getLastUpdDate() {
		return lastUpdDate;
	}

	/**
	 * Set the value related to the column: LAST_UPD_DATE
	 * 
	 * @param lastUpdDate
	 *            the LAST_UPD_DATE value
	 */
	public void setLastUpdDate(java.util.Date lastUpdDate) {
		this.lastUpdDate = lastUpdDate;
	}

	/**
	 * Return the value associated with the column: LAST_UPD_FUNC
	 */
	public java.lang.String getLastUpdFunc() {
		return lastUpdFunc;
	}

	/**
	 * Set the value related to the column: LAST_UPD_FUNC
	 * 
	 * @param lastUpdFunc
	 *            the LAST_UPD_FUNC value
	 */
	public void setLastUpdFunc(java.lang.String lastUpdFunc) {
		this.lastUpdFunc = lastUpdFunc;
	}

	/**
	 * Return the value associated with the column: LAST_UPD_TLR
	 */
	public java.lang.String getLastUpdTlr() {
		return lastUpdTlr;
	}

	/**
	 * Set the value related to the column: LAST_UPD_TLR
	 * 
	 * @param lastUpdTlr
	 *            the LAST_UPD_TLR value
	 */
	public void setLastUpdTlr(java.lang.String lastUpdTlr) {
		this.lastUpdTlr = lastUpdTlr;
	}

	/**
	 * Return the value associated with the column: TIMESTAMPS
	 */
	public java.util.Date getTimestamps() {
		return timestamps;
	}

	/**
	 * Set the value related to the column: TIMESTAMPS
	 * 
	 * @param timestamps
	 *            the TIMESTAMPS value
	 */
	public void setTimestamps(java.util.Date timestamps) {
		this.timestamps = timestamps;
	}

	/**
	 * Return the value associated with the column: MISCFLGS
	 */
	public java.lang.String getMiscflgs() {
		return miscflgs;
	}

	/**
	 * Set the value related to the column: MISCFLGS
	 * 
	 * @param miscflgs
	 *            the MISCFLGS value
	 */
	public void setMiscflgs(java.lang.String miscflgs) {
		this.miscflgs = miscflgs;
	}

	/**
	 * Return the value associated with the column: MISC
	 */
	public java.lang.String getMisc() {
		return misc;
	}

	/**
	 * Set the value related to the column: MISC
	 * 
	 * @param misc
	 *            the MISC value
	 */
	public void setMisc(java.lang.String misc) {
		this.misc = misc;
	}

	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		if (!(obj instanceof resource.bean.pub.RoleInfo))
			return false;
		else {
			resource.bean.pub.RoleInfo roleInfo = (resource.bean.pub.RoleInfo) obj;
			if (null == this.getId() || null == roleInfo.getId())
				return false;
			else
				return (this.getId().equals(roleInfo.getId()));
		}
	}

	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString() {
		return super.toString();
	}

	/** add by zhiyang.he 修改锁定状态 2012-09-6 begin */
	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}

	public java.lang.String getSt() {
		return st;
	}

	public void setSt(java.lang.String st) {
		this.st = st;
	}

	public java.util.Date getCrtDt() {
		return crtDt;
	}

	public void setCrtDt(java.util.Date crtDt) {
		this.crtDt = crtDt;
	}

	public java.lang.String getLastUpdTms() {
		return lastUpdTms;
	}

	public void setLastUpdTms(java.lang.String lastUpdTms) {
		this.lastUpdTms = lastUpdTms;
	}

	public java.lang.String getLastUpdOper() {
		return lastUpdOper;
	}

	public void setLastUpdOper(java.lang.String lastUpdOper) {
		this.lastUpdOper = lastUpdOper;
	}
	/** add by zhiyang.he 修改锁定状态 2012-09-6 end */

}