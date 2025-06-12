package com.game.models.entities.bot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class NodeTest {

    @Test
    void constructorAndFields_ShouldSetValuesCorrectly() {
        Node node = new Node(2, 3, 10, 5);
        assertEquals(2, node.row);
        assertEquals(3, node.col);
        assertEquals(10, node.gCost);
        assertEquals(5, node.hCost);
        assertNull(node.parent);
    }

    @Test
    void fCost_ShouldReturnSumOfGCostAndHCost() {
        Node node = new Node(1, 1, 7, 4);
        assertEquals(11, node.fCost());
    }

    @Test
    void getKey_ShouldReturnRowCommaCol() {
        Node node = new Node(5, 8, 0, 0);
        assertEquals("5,8", node.getKey());
    }

    @Test
    void parentField_ShouldBeAssignable() {
        Node parent = new Node(0, 0, 0, 0);
        Node child = new Node(1, 1, 1, 1);
        child.parent = parent;
        assertSame(parent, child.parent);
    }
}