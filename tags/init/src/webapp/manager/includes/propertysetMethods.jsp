<%@ page import="com.opensymphony.module.propertyset.PropertySet"%>
<%!
private String getTypeName(int type)
{
    switch (type)
    {
        case 1:
            return "Boolean";
        case 2:
            return "Integer";
        case 3:
            return "Long";
        case 4:
            return "Double";
        case 5:
            return "String";
        case 6:
            return "Text";
        case 7:
            return "Date";
        case 8:
            return "Object";
        case 9:
            return "XML";
        case 10:
            return "Data";
        case 11:
            return "Properties";
        default:
            return "Unknown";
    }
}

public String getPropertyValue(PropertySet ps, String key) throws Exception
{
    int type = ps.getType(key);

    switch(type)
    {
        case PropertySet.STRING:
            return ps.getString(key);
        case PropertySet.BOOLEAN:
            return ps.getBoolean(key) + "";
        case PropertySet.INT:
			return ps.getInt(key) + "";
        case PropertySet.LONG:
			return ps.getLong(key) + "";
        case PropertySet.DATE:
            return ps.getDate(key) + "";
		case PropertySet.TEXT:
			return ps.getText(key) + "";
        default:
            return "Cannot display property value";
    }
}

public void setPropertyValue(PropertySet ps, String key, int type, String newVal) throws Exception
{
    try
    {
        switch (type)
        {
            case PropertySet.STRING:
                ps.setString(key, newVal); break;
			case PropertySet.TEXT:
                ps.setText(key, newVal); break;
            case PropertySet.BOOLEAN:
                ps.setBoolean(key, Boolean.valueOf(newVal).booleanValue()); break;
            case PropertySet.INT:
                ps.setInt(key, Integer.valueOf(newVal).intValue()); break;
			case PropertySet.LONG:
                ps.setLong(key, Long.valueOf(newVal).intValue()); break;
			case PropertySet.DOUBLE:
                ps.setDouble(key, Double.valueOf(newVal).intValue()); break;
            case PropertySet.DATE:
                ps.setDate(key, java.text.DateFormat.getDateInstance().parse(newVal)); break;
            default:
                throw new Exception("Type not recognised");
        }
    } catch (Exception e)
    {
        throw e;
    }
}
%>