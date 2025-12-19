# WebView Intent Browser

A deliberately minimal Android WebView app that provides **only basic webpage display**.

This is **not a real browser**.

## What It Does

- Displays a webpage in a WebView  
- Shows the current URL (read-only)  
- Tapping the URL copies it to the clipboard  
- Supports navigation via links inside webpages  
- Shows a loading indicator  
- Handles external intents and can open other apps  

## What It Does Not Do

- No address bar  
- No URL editing  
- No tabs, bookmarks, or downloads  
- No standalone browsing  

## Intent-Only

The app is designed to be launched **only via intents**.  
It loads the provided URL and acts as a simple web viewer.

## Purpose

Built for intentionally restricted or “dumbed-down” devices where full browsers are removed, but essential web functionality (links, login pages, redirects) must still work.

Minimal by design.
