package com.hospital.accompany.task;

import com.hospital.accompany.entity.AccompanyCertificate;
import com.hospital.accompany.entity.CertLeaveRecord;
import com.hospital.accompany.mapper.AccompanyCertificateMapper;
import com.hospital.accompany.mapper.CertLeaveRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class LeaveOverdueTask {

    @Autowired
    private CertLeaveRecordMapper leaveRecordMapper;

    @Autowired
    private AccompanyCertificateMapper certificateMapper;

    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void checkOverdueLeave() {
        LocalDateTime now = LocalDateTime.now();
        List<CertLeaveRecord> overdueRecords = leaveRecordMapper.selectOverdueLeaveRecords(now);

        for (CertLeaveRecord record : overdueRecords) {
            record.setLeaveStatus(3);
            record.setLeaveType(2);
            record.setInvalidReason("超时未归，自动失效");
            record.setInvalidTime(now);
            leaveRecordMapper.updateById(record);

            AccompanyCertificate cert = certificateMapper.selectById(record.getCertId());
            if (cert != null && cert.getCertStatus() != 0) {
                cert.setCertStatus(0);
                cert.setInvalidReason("离院超时未归，陪护证自动失效");
                cert.setInvalidTime(now);
                cert.setCurrentLeaveId(null);
                certificateMapper.updateById(cert);
            }

            log.info("陪护证[{}]离院超时未归自动失效, 离院记录ID: {}", record.getCertNo(), record.getId());
        }
    }
}
