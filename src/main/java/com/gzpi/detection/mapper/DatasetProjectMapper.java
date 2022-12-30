package com.gzpi.detection.mapper;

import com.gzpi.detection.bean.DatasetProject;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DatasetProjectMapper {
    @Select("SELECT id, name, type, clip_size as clipSize FROM project WHERE id=#{id}")
    DatasetProject getProjectById(String id);

    @Select("SELECT id, name, type, clip_size as clipSize FROM project")
    List<DatasetProject> getAllProjects();

    @Insert("insert into project(id, name, type, clip_size) VALUES (#{id}, #{name}, #{type}, #{clipSize})")
    void save(DatasetProject project);
    @Delete("delete from project WHERE id=#{id}")
    void delete(String id);
}
