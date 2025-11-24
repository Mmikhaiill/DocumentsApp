package ru.docapp.documentapp.exceptions;

public class DuplicateDocumentNumberException extends RuntimeException {
    public DuplicateDocumentNumberException(String message) {
        super(message);
    }
}