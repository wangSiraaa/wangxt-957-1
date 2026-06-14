package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.entity.CertTransferRecord;
import com.hospital.accompany.vo.CertTransferRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CertTransferRecordMapper extends BaseMapper<CertTransferRecord> {

    IPage<CertTransferRecordVO> selectTransferPage(Page<CertTransferRecordVO> page,
                                                    @Param("patientId") Long patientId,
                                                    @Param("wardId") Long wardId,
                                                    @Param("oldPersonName") String oldPersonName,
                                                    @Param("newPersonName") String newPersonName);

    CertTransferRecordVO getTransferDetailById(@Param("id") Long id);
}
