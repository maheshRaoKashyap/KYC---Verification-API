package com.kyc.kyc.service;

import com.kyc.kyc.dto.KycDtos.*;
import com.kyc.kyc.entity.KycDetail;
import com.kyc.kyc.event.*;
import com.kyc.kyc.exception.KycException;
import com.kyc.kyc.repository.KycDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {

    private final KycDetailRepository kycDetailRepository;
    private final KycEventProducer kycEventProducer;

    @Transactional
    public KycResponse createKyc(CreateKycRequest request) {
        if (kycDetailRepository.existsByUserId(request.getUserId()))
            throw KycException.conflict("KYC already exists for user: " + request.getUserId());

        KycDetail kyc = KycDetail.builder()
            .userId(request.getUserId())
            .email(request.getEmail())
            .aadhaarNumber(request.getAadhaarNumber())
            .panNumber(request.getPanNumber())
            .voterIdNumber(request.getVoterIdNumber())
            .passportNumber(request.getPassportNumber())
            .drivingLicenseNumber(request.getDrivingLicenseNumber())
            .fullName(request.getFullName())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .fatherName(request.getFatherName())
            .motherName(request.getMotherName())
            .nationality(request.getNationality())
            .permanentAddress(request.getPermanentAddress())
            .currentAddress(request.getCurrentAddress())
            .pinCode(request.getPinCode())
            .district(request.getDistrict())
            .state(request.getState())
            .country(request.getCountry())
            .bankAccountNumber(request.getBankAccountNumber())
            .ifscCode(request.getIfscCode())
            .bankName(request.getBankName())
            .annualIncome(request.getAnnualIncome())
            .sourceOfFunds(request.getSourceOfFunds())
            .status(KycDetail.KycStatus.PENDING)
            .build();

        KycDetail saved = kycDetailRepository.save(kyc);

        kycEventProducer.publishKycEvent(KycEvent.builder()
            .eventType("KYC_CREATED")
            .kycId(saved.getId())
            .userId(saved.getUserId())
            .email(saved.getEmail())
            .fullName(saved.getFullName())
            .newStatus(saved.getStatus().name())
            .build());

        log.info("KYC created for userId={}", request.getUserId());
        return KycResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public KycResponse getKycByUserId(Long userId) {
        KycDetail kyc = kycDetailRepository.findByUserId(userId)
            .orElseThrow(() -> KycException.notFound("KYC not found for userId: " + userId));
        return KycResponse.from(kyc);
    }

    @Transactional(readOnly = true)
    public KycResponse getKycById(Long id) {
        KycDetail kyc = kycDetailRepository.findById(id)
            .orElseThrow(() -> KycException.notFound("KYC not found: " + id));
        return KycResponse.from(kyc);
    }

    @Transactional
    public KycResponse updateKyc(Long userId, UpdateKycRequest request) {
        KycDetail kyc = kycDetailRepository.findByUserId(userId)
            .orElseThrow(() -> KycException.notFound("KYC not found for userId: " + userId));

        if (kyc.getStatus() == KycDetail.KycStatus.VERIFIED)
            throw KycException.badRequest("Cannot update a VERIFIED KYC. Contact support.");

        String oldStatus = kyc.getStatus().name();

        if (request.getAadhaarNumber() != null) kyc.setAadhaarNumber(request.getAadhaarNumber());
        if (request.getPanNumber() != null) kyc.setPanNumber(request.getPanNumber());
        if (request.getVoterIdNumber() != null) kyc.setVoterIdNumber(request.getVoterIdNumber());
        if (request.getPassportNumber() != null) kyc.setPassportNumber(request.getPassportNumber());
        if (request.getFullName() != null) kyc.setFullName(request.getFullName());
        if (request.getDateOfBirth() != null) kyc.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) kyc.setGender(request.getGender());
        if (request.getPermanentAddress() != null) kyc.setPermanentAddress(request.getPermanentAddress());
        if (request.getCurrentAddress() != null) kyc.setCurrentAddress(request.getCurrentAddress());
        if (request.getPinCode() != null) kyc.setPinCode(request.getPinCode());
        if (request.getDistrict() != null) kyc.setDistrict(request.getDistrict());
        if (request.getState() != null) kyc.setState(request.getState());
        if (request.getBankAccountNumber() != null) kyc.setBankAccountNumber(request.getBankAccountNumber());
        if (request.getIfscCode() != null) kyc.setIfscCode(request.getIfscCode());
        if (request.getBankName() != null) kyc.setBankName(request.getBankName());
        if (request.getAnnualIncome() != null) kyc.setAnnualIncome(request.getAnnualIncome());
        if (request.getSourceOfFunds() != null) kyc.setSourceOfFunds(request.getSourceOfFunds());

        // Re-submit for review on update
        if (kyc.getStatus() == KycDetail.KycStatus.REJECTED)
            kyc.setStatus(KycDetail.KycStatus.PENDING);

        KycDetail saved = kycDetailRepository.save(kyc);

        kycEventProducer.publishKycEvent(KycEvent.builder()
            .eventType("KYC_UPDATED")
            .kycId(saved.getId())
            .userId(saved.getUserId())
            .email(saved.getEmail())
            .fullName(saved.getFullName())
            .oldStatus(oldStatus)
            .newStatus(saved.getStatus().name())
            .build());

        return KycResponse.from(saved);
    }

    @Transactional
    public KycResponse updateKycStatus(Long userId, UpdateKycStatusRequest request) {
        KycDetail kyc = kycDetailRepository.findByUserId(userId)
            .orElseThrow(() -> KycException.notFound("KYC not found for userId: " + userId));

        String oldStatus = kyc.getStatus().name();
        kyc.setStatus(request.getStatus());

        if (request.getStatus() == KycDetail.KycStatus.VERIFIED) {
            kyc.setAadhaarVerified(kyc.getAadhaarNumber() != null);
            kyc.setPanVerified(kyc.getPanNumber() != null);
            kyc.setAddressVerified(kyc.getPermanentAddress() != null);
            kyc.setVerifiedBy(request.getVerifiedBy());
            kyc.setVerifiedAt(LocalDateTime.now());
            kyc.setRejectionReason(null);
        } else if (request.getStatus() == KycDetail.KycStatus.REJECTED) {
            kyc.setRejectionReason(request.getRejectionReason());
        }

        KycDetail saved = kycDetailRepository.save(kyc);

        kycEventProducer.publishKycEvent(KycEvent.builder()
            .eventType("KYC_STATUS_CHANGED")
            .kycId(saved.getId())
            .userId(saved.getUserId())
            .email(saved.getEmail())
            .fullName(saved.getFullName())
            .oldStatus(oldStatus)
            .newStatus(saved.getStatus().name())
            .rejectionReason(saved.getRejectionReason())
            .build());

        return KycResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<KycResponse> getAllKycs() {
        return kycDetailRepository.findAll().stream()
            .map(KycResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<KycResponse> getKycsByStatus(KycDetail.KycStatus status) {
        return kycDetailRepository.findByStatus(status).stream()
            .map(KycResponse::from).collect(Collectors.toList());
    }
}
