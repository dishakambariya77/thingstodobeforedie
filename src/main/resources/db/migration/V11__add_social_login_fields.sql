-- Add social login fields to users table
ALTER TABLE users 
ADD COLUMN provider VARCHAR(50) DEFAULT 'LOCAL' NOT NULL,
ADD COLUMN provider_id VARCHAR(255) DEFAULT NULL; 