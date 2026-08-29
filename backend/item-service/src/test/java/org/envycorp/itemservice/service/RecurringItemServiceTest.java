package org.envycorp.itemservice.service;

import org.envycorp.itemservice.exception.ItemAccessDeniedException;
import org.envycorp.itemservice.exception.NoSuchItemWithSpecifiedIdException;
import org.envycorp.itemservice.model.dto.RecurringItemRequestDTO;
import org.envycorp.itemservice.model.dto.RecurringItemResponseDTO;
import org.envycorp.itemservice.model.dto.RecurringItemUpdateDTO;
import org.envycorp.itemservice.model.dto.RecurringItemsSummaryResponseDTO;
import org.envycorp.itemservice.model.entity.BillingCycle;
import org.envycorp.itemservice.model.entity.ItemType;
import org.envycorp.itemservice.model.entity.RecurringItem;
import org.envycorp.itemservice.model.event.ItemEvent;
import org.envycorp.itemservice.repository.RecurringItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecurringItemServiceTest {

    @Mock
    private RecurringItemRepository recurringItemRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ItemEventPublisher publisher;

    @InjectMocks
    private RecurringItemService recurringItemService;

    private UUID userId;
    private UUID itemId;

    private RecurringItem existingItem;

    @BeforeEach
    public void setup() {
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        existingItem = new RecurringItem();
        existingItem.setId(itemId);
        existingItem.setUserId(userId);
        existingItem.setName("Netflix");
        existingItem.setAmount(BigDecimal.valueOf(15));
        existingItem.setCurrency("EUR");
        existingItem.setItemType(ItemType.SUBSCRIPTION);
        existingItem.setBillingCycle(BillingCycle.MONTHLY);
        existingItem.setNextDueDate(LocalDate.now().plusDays(10));
        existingItem.setActive(true);
    }

    @Test
    void getRecurringItemById_returnsItem_whenItBelongsToTheUser() {
        RecurringItemResponseDTO expectedDto = new RecurringItemResponseDTO();
        expectedDto.setId(itemId);
        expectedDto.setName("Netflix");

        when(recurringItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(modelMapper.map(existingItem, RecurringItemResponseDTO.class)).thenReturn(expectedDto);

        RecurringItemResponseDTO result = recurringItemService.getRecurringItemById(userId, itemId);

        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Netflix");
    }

    @Test
    void getRecurringItemById_throwsNotFound_whenItemDoesNotExist() {
        when(recurringItemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recurringItemService.getRecurringItemById(userId, itemId))
                .isInstanceOf(NoSuchItemWithSpecifiedIdException.class);
    }

    @Test
    void getRecurringItemById_throwsAccessDenied_whenItemBelongsToAnotherUser() {
        UUID someoneElsesId = UUID.randomUUID();
        when(recurringItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThatThrownBy(() -> recurringItemService.getRecurringItemById(someoneElsesId, itemId))
                .isInstanceOf(ItemAccessDeniedException.class);
    }

    @Test
    public void getAllRecurringItems_ShouldReturnNotEmptyListOfItems() {
        RecurringItemResponseDTO expectedItem = new RecurringItemResponseDTO();
        expectedItem.setId(itemId);
        expectedItem.setName("Netflix");

        when(recurringItemRepository.findAllByUserId(userId, null, null)).thenReturn(List.of(existingItem));
        when(modelMapper.map(existingItem, RecurringItemResponseDTO.class)).thenReturn(expectedItem);

        List<RecurringItemResponseDTO> result = recurringItemService.getAllRecurringItems(userId, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(itemId);
        assertThat(result.getFirst().getName()).isEqualTo("Netflix");
    }

    @Test
    public void getAllRecurringItems_ShouldReturnEmptyListWhenNoItems() {
        when(recurringItemRepository.findAllByUserId(userId, null, null)).thenReturn(List.of());

        List<RecurringItemResponseDTO> result = recurringItemService.getAllRecurringItems(userId, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    public void getAllRecurringItems_ShouldReturnFilteredItemsByType() {
        RecurringItemResponseDTO expectedItem = new RecurringItemResponseDTO();
        expectedItem.setId(itemId);
        expectedItem.setName("Netflix");

        when(recurringItemRepository.findAllByUserId(userId, ItemType.SUBSCRIPTION, null)).thenReturn(List.of(existingItem));
        when(modelMapper.map(existingItem, RecurringItemResponseDTO.class)).thenReturn(expectedItem);

        List<RecurringItemResponseDTO> result = recurringItemService.getAllRecurringItems(userId, ItemType.SUBSCRIPTION, null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(itemId);
        assertThat(result.getFirst().getName()).isEqualTo("Netflix");
    }

    @Test
    public void createRecurringItem_ShouldReturnCreatedItem() {
        RecurringItemResponseDTO expectedItem = new RecurringItemResponseDTO();
        expectedItem.setId(itemId);
        expectedItem.setName("Netflix");

        RecurringItemRequestDTO newItem = new RecurringItemRequestDTO();
        newItem.setName("Netflix");

        when(recurringItemRepository.save(existingItem)).thenReturn(existingItem);
        when(modelMapper.map(existingItem, RecurringItemResponseDTO.class)).thenReturn(expectedItem);
        when(modelMapper.map(newItem, RecurringItem.class)).thenReturn(existingItem);
        // New stub: publishEvent(...) calls this too, on the exact same
        // "existingItem" instance - a separate overload of map(), needs
        // its own separate stub regardless of the DTO one above.
        when(modelMapper.map(existingItem, ItemEvent.class)).thenReturn(new ItemEvent());

        RecurringItemResponseDTO result = recurringItemService.createRecurringItem(userId, newItem);

        assertThat(result).isEqualTo(expectedItem);
        // Confirms the service actually attempted to publish, not just
        // that nothing crashed - a real behavioral check, same idea as
        // the ArgumentCaptor tests below.
        verify(publisher).publish(org.mockito.ArgumentMatchers.any(ItemEvent.class));
    }

    @Test
    public void updateRecurringItem_ShouldReturnUpdatedItem() {
        RecurringItemResponseDTO expectedItem = new RecurringItemResponseDTO();
        expectedItem.setId(itemId);
        expectedItem.setName("Netflix Updated");

        RecurringItemUpdateDTO updatedItem = new RecurringItemUpdateDTO();
        updatedItem.setName("Netflix Updated");

        when(recurringItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(recurringItemRepository.save(existingItem)).thenReturn(existingItem);
        when(modelMapper.map(existingItem, RecurringItemResponseDTO.class)).thenReturn(expectedItem);
        when(modelMapper.map(existingItem, ItemEvent.class)).thenReturn(new ItemEvent());
        doNothing().when(modelMapper).map(updatedItem, existingItem);

        RecurringItemResponseDTO result = recurringItemService.updateRecurringItem(userId, itemId, updatedItem);

        assertThat(result).isEqualTo(expectedItem);
    }

    @Test
    void pauseRecurringItem_setsActiveToFalse_beforeSaving() {
        when(recurringItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(recurringItemRepository.save(existingItem)).thenReturn(existingItem);
        when(modelMapper.map(existingItem, RecurringItemResponseDTO.class))
                .thenReturn(new RecurringItemResponseDTO());
        when(modelMapper.map(existingItem, ItemEvent.class)).thenReturn(new ItemEvent());

        recurringItemService.pauseRecurringItem(userId, itemId);

        ArgumentCaptor<RecurringItem> itemCaptor = ArgumentCaptor.forClass(RecurringItem.class);
        verify(recurringItemRepository).save(itemCaptor.capture());

        assertThat(itemCaptor.getValue().isActive()).isFalse();
    }

    @Test
    void resumeRecurringItem_setsActiveToTrue_beforeSaving() {
        existingItem.setActive(false);

        when(recurringItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(recurringItemRepository.save(existingItem)).thenReturn(existingItem);
        when(modelMapper.map(existingItem, RecurringItemResponseDTO.class))
                .thenReturn(new RecurringItemResponseDTO());
        when(modelMapper.map(existingItem, ItemEvent.class)).thenReturn(new ItemEvent());

        recurringItemService.resumeRecurringItem(userId, itemId);

        ArgumentCaptor<RecurringItem> itemCaptor = ArgumentCaptor.forClass(RecurringItem.class);
        verify(recurringItemRepository).save(itemCaptor.capture());

        assertThat(itemCaptor.getValue().isActive()).isTrue();
    }

    private RecurringItem createItem(ItemType type, BillingCycle cycle, BigDecimal amount,
                                     boolean active, LocalDate dueDate) {
        RecurringItem item = new RecurringItem();
        item.setId(UUID.randomUUID());
        item.setUserId(userId);
        item.setName("Test item");
        item.setItemType(type);
        item.setBillingCycle(cycle);
        item.setAmount(amount);
        item.setCurrency("EUR");
        item.setActive(active);
        item.setNextDueDate(dueDate);
        return item;
    }
}
