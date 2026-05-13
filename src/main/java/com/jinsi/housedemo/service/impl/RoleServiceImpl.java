package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.Role;
import com.jinsi.housedemo.mapper.RoleMapper;
import com.jinsi.housedemo.service.IRoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
}
