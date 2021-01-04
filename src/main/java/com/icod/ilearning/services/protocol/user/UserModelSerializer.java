package com.icod.ilearning.services.protocol.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.icod.ilearning.data.model.Permission;
import com.icod.ilearning.data.model.User;
import com.icod.ilearning.util.DateTimeUtil;
import com.icod.ilearning.util.HibernateUtil;

import javax.persistence.PersistenceUnitUtil;
import java.io.IOException;

public class UserModelSerializer extends StdSerializer<User> {

    public UserModelSerializer() {
        this(null);
    }

    public UserModelSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User userModel, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException  {
        PersistenceUnitUtil perUtil = HibernateUtil.getSessionFactory().getPersistenceUnitUtil();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", userModel.getId());
        jsonGenerator.writeStringField("fullname", userModel.getName());
        jsonGenerator.writeStringField("email", userModel.getEmail());
        jsonGenerator.writeNumberField("status", userModel.getStatus());
        jsonGenerator.writeStringField("created_at", DateTimeUtil.toString(userModel.getCreatedAt(), DateTimeUtil.FORMAT_DATE_TIME));
        jsonGenerator.writeStringField("updated_at", DateTimeUtil.toString(userModel.getUpdatedAt(), DateTimeUtil.FORMAT_DATE_TIME));
        jsonGenerator.writeStringField("role", userModel.getRole() == null ? null : userModel.getRole().getName());
        jsonGenerator.writeFieldName("permissions");
        jsonGenerator.writeStartArray();
        if(userModel.getRole() !=null && userModel.getRole().getPermissions() != null) {
            for (Permission permission: userModel.getRole().getPermissions()) {
                jsonGenerator.writeString(permission.getName());
            }
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
