package com.openvidu_databases.openvidu_dbbackend.Utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

@Component
public class MapType implements UserType {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapType.class);
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static int[]    types    = new int[] { Types.VARCHAR };
    private static JavaType javaType;

    public  static JavaType getJavaType()
    {
        if (javaType==null)
        {
            synchronized (MapType.class)
            {
                javaType = MAPPER.getTypeFactory().constructType(new TypeReference<Map<String,String>>()
                {
                });        return javaType;
            }
        }
        else
        {
            return javaType;
        }
    }
    @Override
    public int[] sqlTypes() {
        return types;
    }

    @Override
    public Class<?> returnedClass() {
        return Map.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException
    {
        if (x == y)
            return true;
        if (null == x || null == y)
            return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }



    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor arg2, Object arg3) throws HibernateException, SQLException
    {

        String json = rs.getString(names[0]);
        try
        {
            if (json!=null)
            {
                Map<String, String> map = MAPPER.readValue(json, MapType.getJavaType()  );
                return map;
            }
        }
        catch (JsonParseException e)
        {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(),e);
        }
        catch (JsonMappingException e)
        {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(),e);
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(),e);
        }
        return null;
    }
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor arg3) throws HibernateException, SQLException
    {
        String json ="";
        try
        {
            json = MAPPER.writeValueAsString(value);
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(),e);
        }
        st.setString(index, json);
    }



}