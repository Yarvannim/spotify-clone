import numpy as np
import matplotlib.pyplot as plt

# --- Parameters ---
users = np.logspace(0, 6, 1000)      # 1 to 1,000,000 users, logarithmic scale
bitrates = [256, 192, 128]           # kbps
buffer_sec = 5                       # seconds of buffer per user
song_length_sec = 210                # Average song length in seconds (3.5 minutes)
variation = 0.1                      # ±10% shading

# --- Functions for Calculation ---
def kbps_to_gb(bitrate, time_in_sec):
    """Converts a given bitrate and time to GB."""
    return (bitrate * 1000 * time_in_sec) / (8 * 1024**3)

def calculate_total_ram(num_users, bitrate, buffer_sec, song_length_sec):
    """Calculates total RAM for a shared song and user buffers."""
    # RAM required for a single, shared copy of the song
    ram_for_shared_song = kbps_to_gb(bitrate, song_length_sec)
    
    # RAM required for each user's buffer
    ram_for_user_buffers = kbps_to_gb(bitrate, buffer_sec) * num_users
    
    return ram_for_shared_song + ram_for_user_buffers

# --- Data Generation ---
ram_data = {
    b: calculate_total_ram(users, b, buffer_sec, song_length_sec)
    for b in bitrates
}

# --- Plotting ---
plt.figure(figsize=(12, 8))
colors = {256: "red", 192: "blue", 128: "green"}

for b in bitrates:
    ram = ram_data[b]
    plt.plot(users, ram, label=f"{b} kbps", color=colors[b])
    
    # Shaded area ±10%
    plt.fill_between(users, ram * (1 - variation), ram * (1 + variation), color=colors[b], alpha=0.2)

plt.xscale("log")
plt.xlabel("Number of Users")
plt.ylabel("Network Usage (GB)")
plt.title("Network Usage vs Number of Users (Shared Song Model) with 5s of buffer")
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.show()
