package com.kyc.kyc;
import com.kyc.kyc.dto.KycDtos.*;
import com.kyc.kyc.entity.KycDetail;
import com.kyc.kyc.event.*;
import com.kyc.kyc.exception.KycException;
import com.kyc.kyc.repository.KycDetailRepository;
import com.kyc.kyc.service.KycService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KycServiceTest {
    @Mock KycDetailRepository repo;
    @Mock KycEventProducer producer;
    @InjectMocks KycService service;

    @Test void createKyc_success() {
        CreateKycRequest req = CreateKycRequest.builder().userId(1L).email("t@t.com")
            .fullName("Test User").dateOfBirth("1990-01-01").state("KA").country("India").build();
        when(repo.existsByUserId(1L)).thenReturn(false);
        KycDetail saved = KycDetail.builder().id(1L).userId(1L).email("t@t.com")
            .fullName("Test User").status(KycDetail.KycStatus.PENDING).build();
        when(repo.save(any())).thenReturn(saved);
        doNothing().when(producer).publishKycEvent(any());
        KycResponse resp = service.createKyc(req);
        assertThat(resp.getStatus()).isEqualTo("PENDING");
    }
    @Test void createKyc_duplicate_throws() {
        CreateKycRequest req = CreateKycRequest.builder().userId(1L).email("t@t.com")
            .fullName("T").dateOfBirth("1990-01-01").state("KA").country("India").build();
        when(repo.existsByUserId(1L)).thenReturn(true);
        assertThatThrownBy(() -> service.createKyc(req)).isInstanceOf(KycException.class);
    }
    @Test void getKyc_notFound_throws() {
        when(repo.findByUserId(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getKycByUserId(99L)).isInstanceOf(KycException.class);
    }
    @Test void updateVerifiedKyc_throws() {
        KycDetail k = KycDetail.builder().id(1L).userId(1L).status(KycDetail.KycStatus.VERIFIED).build();
        when(repo.findByUserId(1L)).thenReturn(Optional.of(k));
        assertThatThrownBy(() -> service.updateKyc(1L, new UpdateKycRequest()))
            .isInstanceOf(KycException.class).hasMessageContaining("Cannot update a VERIFIED");
    }
}
