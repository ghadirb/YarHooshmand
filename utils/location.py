
import math, time, os
try:
    from plyer import gps
except Exception:
    gps = None

def haversine(lat1, lon1, lat2, lon2):
    # return distance in meters
    R = 6371000
    phi1 = math.radians(lat1); phi2 = math.radians(lat2)
    dphi = math.radians(lat2-lat1); dl = math.radians(lon2-lon1)
    a = math.sin(dphi/2)**2 + math.cos(phi1)*math.cos(phi2)*math.sin(dl/2)**2
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))

def get_current_location(timeout=10):
    """
    Try to get current location using plyer.gps. On Android this requires permissions and
    starting GPS service from the app. This function is a best-effort placeholder.
    """
    if gps is None:
        return None
    # On many platforms plyer.gps requires callback registration; here we return None as placeholder.
    try:
        # platform-specific implementation needed; return None for now.
        return None
    except Exception:
        return None
