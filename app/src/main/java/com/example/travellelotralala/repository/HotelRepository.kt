package com.example.travellelotralala.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class HotelRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val hotelsCollection = firestore.collection("hotels")
    
    // Lấy danh sách khách sạn theo location
    suspend fun getHotelsByLocation(location: String): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = hotelsCollection
                .whereEqualTo("location", location)
                .get()
                .await()
            
            val hotels = snapshot.documents.map { doc ->
                mapOf(
                    "id" to (doc.getString("hotelId") ?: doc.id),
                    "name" to (doc.getString("hotelName") ?: ""),
                    "description" to (doc.getString("hotelDescription") ?: ""),
                    "imageUrl" to (doc.getString("hotelImage") ?: ""),
                    "location" to (doc.getString("location") ?: ""),
                    "rating" to (doc.getString("hotelRating")?.replace("/5", "")?.toDoubleOrNull() ?: 0.0)
                )
            }
            
            Result.success(hotels)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Lấy danh sách loại phòng của một khách sạn
    suspend fun getRoomTypes(hotelId: String): Result<List<Map<String, Any>>> {
        return try {
            Log.d("HotelRepository", "Fetching room types for hotel: $hotelId")
            
            // Thử tìm document với ID chính xác
            val hotelDoc = hotelsCollection.document(hotelId).get().await()
            
            // Nếu document không tồn tại, thử tìm document với ID khác format
            if (!hotelDoc.exists()) {
                Log.d("HotelRepository", "Hotel document not found with ID: $hotelId, trying alternative formats")
                
                // Thử các biến thể của ID
                val alternativeIds = listOf(
                    hotelId.replace("_", ""), // hoian1
                    hotelId.split("_").joinToString("") { it.capitalize() }, // HoiAn1
                    hotelId.split("_").joinToString(" ") { it.capitalize() } // Hoi An 1
                )
                
                Log.d("HotelRepository", "Trying alternative IDs: $alternativeIds")
                
                // Truy vấn để tìm document với hotelId field
                val query = hotelsCollection.whereEqualTo("hotelId", hotelId).get().await()
                if (!query.documents.isEmpty()) {
                    val actualHotelId = query.documents[0].id
                    Log.d("HotelRepository", "Found hotel with actual document ID: $actualHotelId")
                    
                    // Truy vấn roomTypes với ID thực tế
                    val snapshot = hotelsCollection.document(actualHotelId)
                        .collection("roomTypes")
                        .get()
                        .await()
                    
                    Log.d("HotelRepository", "Found ${snapshot.documents.size} room types documents")
                    
                    val roomTypes = snapshot.documents.map { doc ->
                        mapOf(
                            "id" to doc.id,
                            "name" to (doc.getString("name") ?: ""),
                            "description" to (doc.getString("description") ?: ""),
                            "basePrice" to (doc.getString("basePrice") ?: "0"),
                            "imageUrl" to (doc.getString("images") ?: "")
                        )
                    }
                    
                    Log.d("HotelRepository", "Mapped room types: $roomTypes")
                    return Result.success(roomTypes)
                }
                
                // Thử tìm với các ID thay thế
                for (altId in alternativeIds) {
                    val altDoc = hotelsCollection.document(altId).get().await()
                    if (altDoc.exists()) {
                        Log.d("HotelRepository", "Found hotel with alternative ID: $altId")
                        
                        val snapshot = hotelsCollection.document(altId)
                            .collection("roomTypes")
                            .get()
                            .await()
                        
                        Log.d("HotelRepository", "Found ${snapshot.documents.size} room types documents")
                        
                        val roomTypes = snapshot.documents.map { doc ->
                            mapOf(
                                "id" to doc.id,
                                "name" to (doc.getString("name") ?: ""),
                                "description" to (doc.getString("description") ?: ""),
                                "basePrice" to (doc.getString("basePrice") ?: "0"),
                                "imageUrl" to (doc.getString("images") ?: "")
                            )
                        }
                        
                        Log.d("HotelRepository", "Mapped room types: $roomTypes")
                        return Result.success(roomTypes)
                    }
                }
            }
            
            // Nếu tìm thấy document với ID chính xác
            val snapshot = hotelsCollection.document(hotelId)
                .collection("roomTypes")
                .get()
                .await()
            
            Log.d("HotelRepository", "Found ${snapshot.documents.size} room types documents")
            
            val roomTypes = snapshot.documents.map { doc ->
                mapOf(
                    "id" to doc.id,
                    "name" to (doc.getString("name") ?: ""),
                    "description" to (doc.getString("description") ?: ""),
                    "basePrice" to (doc.getString("basePrice") ?: "0"),
                    "imageUrl" to (doc.getString("images") ?: "")
                )
            }
            
            Log.d("HotelRepository", "Mapped room types: $roomTypes")
            Result.success(roomTypes)
        } catch (e: Exception) {
            Log.e("HotelRepository", "Error getting room types: ${e.message}")
            Result.failure(e)
        }
    }
    
    // Lấy danh sách phòng trống của một loại phòng
    suspend fun getAvailableRooms(hotelId: String, roomTypeId: String): Result<List<String>> {
        return try {
            Log.d("HotelRepository", "Fetching available rooms for hotel: $hotelId, roomType: $roomTypeId")
            
            // Thử tìm document với ID chính xác
            val hotelDoc = hotelsCollection.document(hotelId).get().await()
            
            if (!hotelDoc.exists()) {
                Log.d("HotelRepository", "Hotel document not found with ID: $hotelId, trying alternative formats")
                
                // Thử truy vấn để tìm document với hotelId field
                val query = hotelsCollection.whereEqualTo("hotelId", hotelId).get().await()
                if (!query.documents.isEmpty()) {
                    val actualHotelId = query.documents[0].id
                    Log.d("HotelRepository", "Found hotel with actual document ID: $actualHotelId")
                    
                    val snapshot = hotelsCollection.document(actualHotelId)
                        .collection("roomTypes").document(roomTypeId)
                        .collection("rooms")
                        .whereEqualTo("status", "available")
                        .get()
                        .await()
                    
                    val rooms = snapshot.documents.mapNotNull { doc ->
                        doc.getString("roomNumber")
                    }
                    
                    Log.d("HotelRepository", "Found ${rooms.size} available rooms")
                    return Result.success(rooms)
                }
                
                return Result.failure(Exception("Hotel not found"))
            }
            
            // Nếu tìm thấy document với ID chính xác
            val snapshot = hotelsCollection.document(hotelId)
                .collection("roomTypes").document(roomTypeId)
                .collection("rooms")
                .whereEqualTo("status", "available")
                .get()
                .await()
            
            val rooms = snapshot.documents.mapNotNull { doc ->
                doc.getString("roomNumber")
            }
            
            Log.d("HotelRepository", "Found ${rooms.size} available rooms with direct ID")
            Result.success(rooms)
        } catch (e: Exception) {
            Log.e("HotelRepository", "Error getting available rooms: ${e.message}")
            Result.failure(e)
        }
    }
    
    // Cập nhật trạng thái phòng thành đã đặt
    suspend fun bookRoom(hotelId: String, roomTypeId: String, roomNumber: String): Result<Unit> {
        return try {
            Log.d("HotelRepository", "Attempting to book room: hotelId=$hotelId, roomTypeId=$roomTypeId, roomNumber=$roomNumber")
            
            // Thử tìm document với ID chính xác
            val hotelDoc = hotelsCollection.document(hotelId).get().await()
            
            if (!hotelDoc.exists()) {
                Log.d("HotelRepository", "Hotel document not found with ID: $hotelId, trying alternative formats")
                
                // Thử truy vấn để tìm document với hotelId field
                val query = hotelsCollection.whereEqualTo("hotelId", hotelId).get().await()
                if (!query.documents.isEmpty()) {
                    val actualHotelId = query.documents[0].id
                    Log.d("HotelRepository", "Found hotel with actual document ID: $actualHotelId")
                    
                    // Tìm phòng với ID thực tế
                    val roomsSnapshot = hotelsCollection.document(actualHotelId)
                        .collection("roomTypes").document(roomTypeId)
                        .collection("rooms")
                        .whereEqualTo("roomNumber", roomNumber)
                        .get()
                        .await()
                    
                    if (roomsSnapshot.documents.isEmpty()) {
                        // Nếu không tìm thấy phòng, tạo một phòng mới với trạng thái booked
                        Log.d("HotelRepository", "Room not found, creating a new room with booked status")
                        val roomData = mapOf(
                            "roomNumber" to roomNumber,
                            "status" to "booked",
                            "bookedAt" to com.google.firebase.Timestamp.now() // Thêm timestamp để biết khi nào phòng được đặt
                        )
                        
                        hotelsCollection.document(actualHotelId)
                            .collection("roomTypes").document(roomTypeId)
                            .collection("rooms").document()
                            .set(roomData)
                            .await()
                        
                        Log.d("HotelRepository", "Successfully created and booked new room")
                        return Result.success(Unit)
                    }
                    
                    val roomDocId = roomsSnapshot.documents[0].id
                    
                    // Cập nhật trạng thái phòng
                    hotelsCollection.document(actualHotelId)
                        .collection("roomTypes").document(roomTypeId)
                        .collection("rooms").document(roomDocId)
                        .update(
                            mapOf(
                                "status" to "booked",
                                "bookedAt" to com.google.firebase.Timestamp.now()
                            )
                        )
                        .await()
                    
                    Log.d("HotelRepository", "Successfully updated room status to booked")
                    return Result.success(Unit)
                }
                
                // Nếu không tìm thấy khách sạn, tạo một document mới cho khách sạn và phòng
                Log.d("HotelRepository", "Hotel not found, creating new hotel document")
                
                // Tạo phòng với trạng thái booked
                val roomData = mapOf(
                    "roomNumber" to roomNumber,
                    "status" to "booked",
                    "bookedAt" to com.google.firebase.Timestamp.now()
                )
                
                // Tạo document mới cho khách sạn nếu không tồn tại
                hotelsCollection.document(hotelId)
                    .collection("roomTypes").document(roomTypeId)
                    .collection("rooms").document()
                    .set(roomData)
                    .await()
                
                Log.d("HotelRepository", "Created new hotel document and booked room")
                return Result.success(Unit)
            }
            
            // Nếu tìm thấy document với ID chính xác
            val roomsSnapshot = hotelsCollection.document(hotelId)
                .collection("roomTypes").document(roomTypeId)
                .collection("rooms")
                .whereEqualTo("roomNumber", roomNumber)
                .get()
                .await()
            
            if (roomsSnapshot.documents.isEmpty()) {
                // Nếu không tìm thấy phòng, tạo một phòng mới với trạng thái booked
                Log.d("HotelRepository", "Room not found, creating a new room with booked status")
                val roomData = mapOf(
                    "roomNumber" to roomNumber,
                    "status" to "booked",
                    "bookedAt" to com.google.firebase.Timestamp.now()
                )
                
                hotelsCollection.document(hotelId)
                    .collection("roomTypes").document(roomTypeId)
                    .collection("rooms").document()
                    .set(roomData)
                    .await()
                
                Log.d("HotelRepository", "Successfully created and booked new room")
                return Result.success(Unit)
            }
            
            val roomDocId = roomsSnapshot.documents[0].id
            
            // Cập nhật trạng thái phòng
            hotelsCollection.document(hotelId)
                .collection("roomTypes").document(roomTypeId)
                .collection("rooms").document(roomDocId)
                .update(
                    mapOf(
                        "status" to "booked",
                        "bookedAt" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()
            
            Log.d("HotelRepository", "Successfully updated room status to booked")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("HotelRepository", "Error booking room: ${e.message}", e)
            // Trả về failure để ViewModel biết có lỗi xảy ra
            Result.failure(e)
        }
    }

    // Lấy thông tin chi tiết của một khách sạn
    suspend fun getHotelById(hotelId: String): Result<Map<String, Any>> {
        return try {
            Log.d("HotelRepository", "Fetching hotel with ID: $hotelId")
            
            // Thử tìm khách sạn bằng cách truy vấn theo trường hotelId
            val query = hotelsCollection.whereEqualTo("hotelId", hotelId).get().await()
            
            if (query.documents.isEmpty()) {
                // Nếu không tìm thấy, thử lấy trực tiếp từ document ID
                val directDoc = hotelsCollection.document(hotelId).get().await()
                
                if (!directDoc.exists()) {
                    Log.e("HotelRepository", "Hotel not found with ID: $hotelId")
                    return Result.failure(Exception("Hotel not found"))
                }
                
                val hotel = mapOf(
                    "id" to (directDoc.getString("hotelId") ?: directDoc.id),
                    "name" to (directDoc.getString("hotelName") ?: ""),
                    "description" to (directDoc.getString("hotelDescription") ?: ""),
                    "imageUrl" to (directDoc.getString("hotelImage") ?: ""),
                    "location" to (directDoc.getString("location") ?: ""),
                    "rating" to (directDoc.getString("hotelRating")?.replace("/5", "")?.toDoubleOrNull() ?: 0.0)
                )
                
                Log.d("HotelRepository", "Found hotel directly: ${hotel["name"]}")
                return Result.success(hotel)
            }
            
            // Nếu tìm thấy qua query
            val doc = query.documents.first()
            val hotel = mapOf(
                "id" to (doc.getString("hotelId") ?: doc.id),
                "name" to (doc.getString("hotelName") ?: ""),
                "description" to (doc.getString("hotelDescription") ?: ""),
                "imageUrl" to (doc.getString("hotelImage") ?: ""),
                "location" to (doc.getString("location") ?: ""),
                "rating" to (doc.getString("hotelRating")?.replace("/5", "")?.toDoubleOrNull() ?: 0.0)
            )
            
            Log.d("HotelRepository", "Found hotel via query: ${hotel["name"]}")
            Result.success(hotel)
        } catch (e: Exception) {
            Log.e("HotelRepository", "Error getting hotel: ${e.message}")
            Result.failure(e)
        }
    }
}



