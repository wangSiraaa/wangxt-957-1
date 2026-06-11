package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.entity.AccompanyCertificate;
import com.hospital.accompany.vo.CertificateVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AccompanyCertificateMapper extends BaseMapper<AccompanyCertificate> {

    @Select("SELECT COUNT(*) FROM accompany_certificate " +
            "WHERE patient_id = #{patientId} AND cert_status = 1 AND deleted = 0")
    Integer countValidByPatientId(@Param("patientId") Long patientId);

    CertificateVO getCertificateDetailById(@Param("id") Long id);

    CertificateVO getCertificateByNo(@Param("certNo") String certNo);

    IPage<CertificateVO> selectCertificatePage(Page<CertificateVO> page,
                                                @Param("wardId") Long wardId,
                                                @Param("certStatus") Integer certStatus,
                                                @Param("patientName") String patientName,
                                                @Param("personName") String personName,
                                                @Param("certNo") String certNo);
}
