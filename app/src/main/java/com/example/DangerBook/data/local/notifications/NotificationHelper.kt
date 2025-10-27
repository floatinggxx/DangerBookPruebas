package com.example.DangerBook.data.local.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.DangerBook.MainActivity
import com.example.uinavegacion.R

// Helper para enviar notificaciones locales en DangerBook
object NotificationHelper {

    private const val CHANNEL_ID = "dangerbook_appointments"
    private const val CHANNEL_NAME = "Citas DangerBook"
    private const val CHANNEL_DESCRIPTION = "Notificaciones sobre tus citas en Studio Danger"

    // Crear el canal de notificaciones (necesario en Android 8+)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Verificar si tenemos permiso para notificaciones (Android 13+)
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // En versiones antiguas no se necesita permiso explícito
        }
    }

    // Enviar notificación de cita confirmada
    @SuppressLint("MissingPermission")
    fun sendAppointmentConfirmedNotification(
        context: Context,
        appointmentId: Long,
        dateTime: String
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            appointmentId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Cambiar por ícono de la app
            .setContentTitle("¡Cita Confirmada!")
            .setContentText("Tu cita para el $dateTime ha sido confirmada en Studio Danger.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Tu cita para el $dateTime ha sido confirmada en Studio Danger. ¡Te esperamos!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(appointmentId.toInt(), notification)
    }

    // Enviar notificación de cita cancelada
    @SuppressLint("MissingPermission")
    fun sendAppointmentCancelledNotification(
        context: Context,
        appointmentId: Long,
        dateTime: String
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            appointmentId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Cita Cancelada")
            .setContentText("Tu cita para el $dateTime ha sido cancelada.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(appointmentId.toInt(), notification)
    }

    // Enviar notificación de recordatorio (1 día antes)
    @SuppressLint("MissingPermission")
    fun sendAppointmentReminderNotification(
        context: Context,
        appointmentId: Long,
        dateTime: String
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            appointmentId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio de Cita")
            .setContentText("Mañana tienes cita en Studio Danger a las $dateTime")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("No olvides tu cita mañana a las $dateTime en Studio Danger. ¡Te esperamos!"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(appointmentId.toInt() + 1000, notification)
    }

    // Enviar notificación para barberos (nueva cita asignada)
    @SuppressLint("MissingPermission")
    fun sendNewAppointmentForBarberNotification(
        context: Context,
        appointmentId: Long,
        clientName: String,
        dateTime: String
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            appointmentId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nueva Cita Asignada")
            .setContentText("$clientName ha agendado una cita contigo para el $dateTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(appointmentId.toInt() + 2000, notification)
    }
}