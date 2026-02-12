package com.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
}
