package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemRepository repository;      // a fake repository

    @InjectMocks
    private ItemController controller; 

    @Test
    void getItemReturnsItemWhenExists() {
       
        String sku = "TEST-SKU";
        Item item = new Item();
        item.setId(1L);
        item.setSku(sku);
        

        when(repository.findById(1L)).thenReturn(Optional.of(item));

        
        ResponseEntity<Item> response = controller.getItem(1L);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(item);

    }

    @Test
    void getItemReturnsNotFoundWhenDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = controller.getItem(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


}

