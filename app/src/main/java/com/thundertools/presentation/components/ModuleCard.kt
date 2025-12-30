package com.thundertools.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thundertools.data.models.ModuleItem
import com.thundertools.presentation.theme.ElectricBlue

@Composable
fun ModuleCard(
    module: ModuleItem,
    isOnline: Boolean = false,
    onClick: () -> Unit
) {
    val isDisabled = module.requiresConnection && !isOnline
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = !isDisabled,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDisabled) Color.DarkGray.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isDisabled) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Requiere conexión",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = module.name,
                        tint = ElectricBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Nombre del módulo
            Text(
                text = module.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Descripción pequeña
            Text(
                text = module.description,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}