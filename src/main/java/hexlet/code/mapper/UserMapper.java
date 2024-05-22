package hexlet.code.mapper;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class UserMapper {

    @Mapping(target = "passwordDigest", source = "password")
    public abstract User map(UserCreateDTO dto);

    @Mapping(target = "createdAt", source = "createdAt")
    public abstract UserDTO map(User model);

    @Mapping(target = "passwordDigest", source = "password")
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);
}
