package com.stockmarket.stockmarket_core.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmarket.stockmarket_core.dto.wallet.NewWalletResponse;
import com.stockmarket.stockmarket_core.exception.WalletNotFoundException;
import com.stockmarket.stockmarket_core.model.AuditLog;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.repository.AuditLogRepository;
import com.stockmarket.stockmarket_core.repository.WalletRepository;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AuditLogRepository logRepository;

    @InjectMocks
    private WalletService walletService;

    @Captor
    private ArgumentCaptor<Wallet> walletCaptor;

    @Captor
    private ArgumentCaptor<AuditLog> logCaptor;


    @Test
    public void stockExistingTest(){
        Long walletId = 1L;
        when(walletRepository.existsById(walletId)).thenReturn(true);

        assertTrue(walletService.walletExists(walletId));
        assertFalse(walletService.walletExists(5l));
    }

    @Test
    public void walletCreationTest(){
        NewWalletResponse response = walletService.createWallet();

        assertNotNull(response);

        verify(walletRepository).save(walletCaptor.capture());
        Wallet createdWallet = walletCaptor.getValue();
        verify(logRepository).save(logCaptor.capture());
        AuditLog auditLog = logCaptor.getValue();

        assertEquals(response.id(), createdWallet.getId());
        assertEquals(LogActionType.WALLET_CREATE, auditLog.getActionType());

        assertEquals(createdWallet, auditLog.getWallet());
    }

    @Test
    public void shouldThrowWalletNotFoundExceptionDuringDeletionTest(){
        Long nonExistentWalletId = 1L;
        when(walletRepository.findById(nonExistentWalletId)).thenReturn(Optional.empty());

        WalletNotFoundException e = assertThrows(
            WalletNotFoundException.class, 
            () -> walletService.deleteWallet(nonExistentWalletId)
        );
        assertEquals(
            "The wallet with id: %s does not exist or is inactive.".formatted(nonExistentWalletId),
            e.getMessage()
        );
        verifyNoInteractions(logRepository);
    }

    @Test
    public void walletSuccessfulDeletionTest(){
        Long walletId = 1l;
        Wallet targetWallet = new Wallet();
        targetWallet.setIsActive(true);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(targetWallet));
        
        walletService.deleteWallet(walletId); 

        assertFalse(targetWallet.getIsActive());
        
        verify(logRepository).save(logCaptor.capture());
        AuditLog deletionLog = logCaptor.getValue();

        assertEquals(LogActionType.WALLET_DELETE, deletionLog.getActionType());
        assertEquals(LogStatus.INFO, deletionLog.getStatus());
        assertEquals(targetWallet, deletionLog.getWallet());
    }
    
}
